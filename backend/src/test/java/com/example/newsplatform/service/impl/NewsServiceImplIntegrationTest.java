package com.example.newsplatform.service.impl;

import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NewsServiceImplIntegrationTest {

    @Autowired
    private NewsService newsService;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("integration_user");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setActive(true);
        userRepository.save(testUser);

        auth = new UsernamePasswordAuthenticationToken(testUser.getUsername(), "password", Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createNewsShouldSaveAndReturnDto() {
        // Given
        NewsCreateRequestDto request = new NewsCreateRequestDto();
        request.setTitle("Integration Test Title");
        request.setContent("Some content");

        // When
        NewsDto saved = newsService.create(request, auth);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Integration Test Title", saved.getTitle());
        assertEquals(testUser.getId(), saved.getAuthorId());
    }

    @Test
    void testDeleteNews() {
        // Given
        NewsCreateRequestDto request = new NewsCreateRequestDto();
        request.setTitle("To Be Deleted");
        request.setContent("Content");
        NewsDto saved = newsService.create(request, auth);
        Long newsId = saved.getId();

        // When
        newsService.delete(newsId, SecurityContextHolder.getContext().getAuthentication());

        // Then
        Optional<News> found = newsRepository.findById(newsId);
        assertFalse(found.isPresent());
    }
}