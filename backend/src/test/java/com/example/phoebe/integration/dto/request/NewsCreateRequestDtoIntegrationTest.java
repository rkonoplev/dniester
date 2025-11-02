package com.example.phoebe.integration.dto.request;

import com.example.phoebe.dto.request.NewsCreateRequestDto;
import com.example.phoebe.entity.User;
import com.example.phoebe.integration.AbstractIntegrationTest;
import com.example.phoebe.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewsCreateRequestDtoIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private Validator validator;

    @Autowired
    private UserRepository userRepository;

    private User author;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        author = new User("creator_user", "pass", "creator@test.com", true);
        userRepository.save(author);
    }

    @Test
    void whenCreateWithValidDto_thenNoViolations() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto();
        dto.setTitle("Valid Title");
        dto.setContent("Some content here.");

        Set<ConstraintViolation<NewsCreateRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void whenTitleIsBlank_thenViolation() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto();
        dto.setContent("Some content here.");

        Set<ConstraintViolation<NewsCreateRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
    }
}
