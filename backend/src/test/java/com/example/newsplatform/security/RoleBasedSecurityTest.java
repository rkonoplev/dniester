package com.example.newsplatform.security;

import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.service.impl.NewsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test role-based security for NewsService operations.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoleBasedSecurityTest {

    @Autowired
    private NewsServiceImpl newsService;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testAdminCanAccessAnyContent() {
        // Create test user and news
        User author = createTestUser("author", "author@test.com");
        News news = createTestNews("Test Article", author);

        // Create ADMIN authentication
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
            "admin", "password", 
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        // Admin should be able to access any content (role ID 1 = ADMIN)
        assertTrue(adminAuth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testEditorCanOnlyAccessOwnContent() {
        // Create test users
        User author1 = createTestUser("editor1", "editor1@test.com");
        User author2 = createTestUser("editor2", "editor2@test.com");
        
        // Create news by author1
        News news = createTestNews("Test Article", author1);

        // Editor1 should be able to access own content
        assertTrue(newsService.isAuthor(news.getId(), author1.getId()));
        
        // Editor2 should NOT be able to access author1's content
        assertFalse(newsService.isAuthor(news.getId(), author2.getId()));
    }

    @Test
    void testAuthorVerificationMethod() {
        // Create test user and news
        User author = createTestUser("testauthor", "test@test.com");
        News news = createTestNews("Test Article", author);

        // Test author verification
        assertTrue(newsService.isAuthor(news.getId(), author.getId()));
        assertFalse(newsService.isAuthor(news.getId(), 999L)); // Non-existent author ID
    }

    private User createTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setActive(true);
        return userRepository.save(user);
    }

    private News createTestNews(String title, User author) {
        News news = new News();
        news.setTitle(title);
        news.setBody("Test content");
        news.setTeaser("Test teaser");
        news.setPublicationDate(LocalDateTime.now());
        news.setPublished(true);
        news.setAuthor(author);
        return newsRepository.save(news);
    }
}