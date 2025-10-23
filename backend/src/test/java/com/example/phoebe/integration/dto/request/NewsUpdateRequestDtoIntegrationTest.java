package com.example.phoebe.dto.request;

import com.example.phoebe.dto.response.NewsDto;
import com.example.phoebe.entity.User;
import com.example.phoebe.repository.UserRepository;
import com.example.phoebe.service.NewsService;
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
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("test_updater");
        testUser.setEmail("updater@test.com");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setActive(true);
        userRepository.save(testUser);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser.getUsername(), "password", Collections.emptyList())
        );
    }

    @Test
    void updateNewsWithPartialDtoShouldUpdateOnlyProvidedFields() {
        // Given: An existing news article
        NewsCreateRequestDto createDto = new NewsCreateRequestDto();
        createDto.setTitle("Original Title");
        createDto.setContent("Original content.");
        NewsDto savedNews = newsService.create(createDto, SecurityContextHolder.getContext().getAuthentication());

        // When: We update it with a DTO containing only a new title
        NewsUpdateRequestDto updateDto = new NewsUpdateRequestDto(
                "Updated Title",
                savedNews.getBody(),
                savedNews.getTeaser(),
                savedNews.isPublished(),
                null
        );
        NewsDto updatedNews = newsService.update(savedNews.getId(), updateDto, SecurityContextHolder.getContext().getAuthentication());

        // Then: The title is updated, but the content remains the same
        assertEquals("Updated Title", updatedNews.getTitle());
        assertEquals("Original content.", updatedNews.getBody());
    }
}