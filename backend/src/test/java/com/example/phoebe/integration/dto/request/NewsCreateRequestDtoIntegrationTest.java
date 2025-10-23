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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class NewsCreateRequestDtoIntegrationTest {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User author;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setUsername("creator_user");
        author.setEmail("creator@test.com");
        author.setPassword(passwordEncoder.encode("password"));
        author.setActive(true);
        userRepository.save(author);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(author.getUsername(), "password", Collections.emptyList())
        );
    }

    @Test
    void whenCreateNewsWithValidDtoThenNewsIsCreated() {
        // Given
        NewsCreateRequestDto dto = new NewsCreateRequestDto();
        dto.setTitle("Integration Test Title");
        dto.setContent("Integration test body content.");

        // When
        NewsDto result = newsService.create(dto, SecurityContextHolder.getContext().getAuthentication());

        // Then
        assertNotNull(result.getId());
        assertEquals("Integration Test Title", result.getTitle());
        assertEquals("Integration test body content.", result.getBody());
        assertEquals(author.getId(), result.getAuthorId());
    }
}