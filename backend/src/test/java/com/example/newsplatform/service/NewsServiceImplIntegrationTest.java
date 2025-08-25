package com.example.newsplatform.service;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.entity.Term;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.TermRepository;
import com.example.newsplatform.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for NewsServiceImpl using Spring Boot context with H2 database.
 * Profile "test" must be configured with in-memory H2 datasource (application-test.yml).
 */
@SpringBootTest
@ActiveProfiles("test") // forces Spring to load application-test.yml with H2 config
@Transactional
class NewsServiceImplIntegrationTest {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TermRepository termRepository;

    private User testUser;
    private Term testTerm;

    @BeforeEach
    void setup() {
        // Insert sample user (author)
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setStatus(true);
        userRepository.save(testUser);

        // Insert sample category (term)
        testTerm = new Term();
        testTerm.setId(1L);
        testTerm.setName("Technology");
        testTerm.setVocabulary("category");
        termRepository.save(testTerm);
    }

    @Test
    void testCreateNews() {
        NewsCreateRequest request = new NewsCreateRequest();
        request.setTitle("Integration Test News");
        request.setBody("This is the body.");
        request.setTeaser("This is teaser.");
        request.setPublicationDate(LocalDateTime.now());
        request.setPublished(true);
        request.setAuthorId(testUser.getId());
        request.setCategoryId(testTerm.getId());

        NewsDto saved = newsService.create(request);

        assertNotNull(saved.getId(), "News ID should not be null after save");
        assertEquals("Integration Test News", saved.getTitle());
        assertEquals("Technology", saved.getCategory());
        assertEquals(testUser.getId(), saved.getAuthorId());
        assertTrue(saved.isPublished());
    }

    @Test
    void testUpdateNews() {
        // First create news
        NewsCreateRequest request = new NewsCreateRequest();
        request.setTitle("News Title");
        request.setBody("Body");
        request.setPublicationDate(LocalDateTime.now());
        request.setPublished(false);
        request.setAuthorId(testUser.getId());
        request.setCategoryId(testTerm.getId());

        NewsDto saved = newsService.create(request);

        // Then update it
        NewsUpdateRequest update = new NewsUpdateRequest();
        update.setTitle("Updated News Title");
        update.setBody("Updated body");
        update.setPublished(true);

        NewsDto updated = newsService.update(saved.getId(), update);

        assertEquals("Updated News Title", updated.getTitle());
        assertEquals("Updated body", updated.getBody());
        assertTrue(updated.isPublished());
    }

    @Test
    void testDeleteNews() {
        // Create news
        NewsCreateRequest request = new NewsCreateRequest();
        request.setTitle("Temp News");
        request.setBody("Some body");
        request.setPublicationDate(LocalDateTime.now());
        request.setPublished(true);
        request.setAuthorId(testUser.getId());
        request.setCategoryId(testTerm.getId());

        NewsDto saved = newsService.create(request);
        Long newsId = saved.getId();
        assertNotNull(newsId);

        // Delete
        newsService.delete(newsId);

        // Verify not found after delete
        assertThrows(RuntimeException.class, () -> newsService.getPublishedById(newsId));
    }
}