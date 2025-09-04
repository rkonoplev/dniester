package com.example.newsplatform.service;

import com.example.newsplatform.NewsPlatformApplication;
import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.entity.Term;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.exception.NotFoundException;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.repository.TermRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Integration test for NewsService.
 * Uses test profile which has its own security configuration.
 */
@SpringBootTest(classes = {
    com.example.newsplatform.NewsPlatformApplication.class,
    com.example.newsplatform.config.TestSecurityConfig.class
}, properties = {
    "spring.main.allow-bean-definition-overriding=true"
})
@ActiveProfiles("test")
@Transactional
public class NewsServiceImplIntegrationTest {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TermRepository termRepository;

    @Test
    void testDeleteNews() {
        // Create test user
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setActive(true);
        testUser = userRepository.save(testUser);

        // Create test category term
        Term testTerm = new Term();
        testTerm.setName("Test Category");
        testTerm.setVocabulary("category");
        testTerm = termRepository.save(testTerm);

        // Create news
        NewsCreateRequest request = new NewsCreateRequest();
        request.setTitle("Temp News");
        request.setBody("Some body");
        request.setPublicationDate(LocalDateTime.now());
        request.setPublished(true);
        request.setAuthorId(testUser.getId());
        request.setCategoryId(testTerm.getId());

        NewsDto saved = newsService.create(request);
        Long newsId = saved.id();
        assertNotNull(newsId, "News should be created successfully");

        // Delete the news
        newsService.delete(newsId);

        // Verify that news is not found after deletion
        assertThrows(NotFoundException.class,
                () -> newsService.getPublishedById(newsId),
                "News should be deleted and not found"
        );
    }
}