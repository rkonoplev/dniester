package com.example.newsplatform.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NewsDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void newsCreateRequestDto_WithValidData_ShouldPassValidation() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto();
        dto.setTitle("Valid Title");
        dto.setBody("Valid body content");
        dto.setAuthorId(1L);
        dto.setCategoryId(1L);
        dto.setPublicationDate(LocalDateTime.now());
        dto.setPublished(true);

        Set<ConstraintViolation<NewsCreateRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void newsCreateRequestDto_WithBlankTitle_ShouldFailValidation() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto();
        dto.setTitle("");
        dto.setBody("Valid body");
        dto.setAuthorId(1L);
        dto.setCategoryId(1L);
        dto.setPublicationDate(LocalDateTime.now());

        Set<ConstraintViolation<NewsCreateRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title is required")));
    }

    @Test
    void newsCreateRequestDto_WithNullRequiredFields_ShouldFailValidation() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto();
        dto.setTitle("Valid Title");
        dto.setBody("Valid body");

        Set<ConstraintViolation<NewsCreateRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Category ID is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Author ID is required")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Publication date is required")));
    }

    @Test
    void newsUpdateRequestDto_WithValidData_ShouldPassValidation() {
        NewsUpdateRequestDto dto = new NewsUpdateRequestDto();
        dto.setTitle("Updated Title");
        dto.setBody("Updated body");
        dto.setCategoryId(2L);

        Set<ConstraintViolation<NewsUpdateRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void newsUpdateRequestDto_WithTooLongTitle_ShouldFailValidation() {
        NewsUpdateRequestDto dto = new NewsUpdateRequestDto();
        dto.setTitle("A".repeat(256)); // Exceeds 255 character limit

        Set<ConstraintViolation<NewsUpdateRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }
}