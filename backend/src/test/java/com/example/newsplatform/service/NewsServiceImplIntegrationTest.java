package com.example.newsplatform.service;

import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.entity.Term;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.TermRepository;
import com.example.newsplatform.repository.UserRepository;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NewsServiceImplIntegrationTest {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Term testTerm;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("integration_user");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setActive(true);
        userRepository.save(testUser);

        testTerm = new Term();
        testTerm.setName("Integration Test");
        termRepository.save(testTerm);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser.getUsername(), "password", Collections.emptyList())
        );
    }

    @Test
    void createNews_shouldSaveAndReturnDto() {
        // Given
        NewsCreateRequestDto request = new NewsCreateRequestDto();
        request.setTitle("Integration Test Title");
        request.setContent("Some content");

        // When
        NewsDto saved = newsService.create(request);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Integration Test Title", saved.getTitle());
        assertEquals(testUser.getId(), saved.getAuthorId());
    }
}