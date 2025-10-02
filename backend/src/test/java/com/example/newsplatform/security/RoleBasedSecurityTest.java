package com.example.newsplatform.security;

import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.Role;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.security.AuthorVerification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RoleBasedSecurityTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorVerification authorVerification;

    private User adminUser;
    private User editorUser;
    private News editorsNews;

    @BeforeEach
    void setUp() {
        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        Role editorRole = new Role();
        editorRole.setName("EDITOR");

        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("password");
        adminUser.setActive(true);
        adminUser.setRoles(Set.of(adminRole));
        userRepository.save(adminUser);

        editorUser = new User();
        editorUser.setUsername("editor");
        editorUser.setPassword("password");
        editorUser.setActive(true);
        editorUser.setRoles(Set.of(editorRole));
        userRepository.save(editorUser);

        editorsNews = new News();
        editorsNews.setTitle("Editor's News");
        editorsNews.setAuthor(editorUser);
        newsRepository.save(editorsNews);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testAdminCanAccessAnyContent() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(adminUser.getUsername(), "password")
        );
        assertTrue(authorVerification.hasAccessToNews(adminUser, editorsNews.getId()));
    }

    @Test
    void testEditorCanOnlyAccessOwnContent() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(editorUser.getUsername(), "password")
        );
        assertTrue(authorVerification.hasAccessToNews(editorUser, editorsNews.getId()));

        // Create another user's news
        News otherNews = new News();
        otherNews.setAuthor(adminUser);
        newsRepository.save(otherNews);

        assertFalse(authorVerification.hasAccessToNews(editorUser, otherNews.getId()));
    }

    @Test
    void testAuthorVerificationMethod() {
        // Test access for the actual author
        assertTrue(authorVerification.isAuthor(editorUser, editorsNews));

        // Test access for a different user
        assertFalse(authorVerification.isAuthor(adminUser, editorsNews));
    }
}
