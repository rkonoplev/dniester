package com.example.newsplatform.dto.request;

import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.TermRepository;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NewsUpdateRequestDtoIntegrationTest {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create and save a user for the tests
        testUser = new User();
        testUser.setUsername("test_updater");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setActive(true);
        userRepository.save(testUser);

        // Set up security context to simulate an authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser.getUsername(), "password", Collections.emptyList())
        );
    }

    @Test
    void updateNews_WithPartialDto_ShouldUpdateOnlyProvidedFields() {
        // Given: An existing news article
        NewsCreateRequestDto createDto = new NewsCreateRequestDto();
        createDto.setTitle("Original Title");
        createDto.setContent("Original content.");
        NewsDto savedNews = newsService.create(createDto);

        // When: We update it with a DTO containing only a new title
        NewsUpdateRequestDto updateDto = new NewsUpdateRequestDto("Updated Title", savedNews.getContent(), savedNews.isPublished());
        NewsDto updatedNews = newsService.update(savedNews.getId(), updateDto);

        // Then: The title is updated, but the content remains the same
        assertEquals("Updated Title", updatedNews.getTitle());
        assertEquals("Original content.", updatedNews.getContent());
    }
}