package com.example.newsplatform.dto;

import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class NewsDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // --- NewsCreateRequestDto Tests ---

    @Test
    void whenCreateDtoIsValidThenNoViolations() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto(); dto.setTitle("Valid Title"); dto.setContent("Valid Content"); dto.setTeaser("Optional teaser");
        Set<ConstraintViolation<NewsCreateRequestDto>> violations = validator.validate(dto);
        assertEquals(0, violations.size());
    }

    @Test
    void whenCreateDtoTitleIsBlankThenViolation() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto(); dto.setTitle(""); dto.setContent("Valid Content");
        Set<ConstraintViolation<NewsCreateRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenCreateDtoContentIsBlankThenViolation() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto(); dto.setTitle("Valid Title"); dto.setContent("");
        Set<ConstraintViolation<NewsCreateRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenCreateDtoTitleExceedsLimitThenViolation() {
        NewsCreateRequestDto dto = new NewsCreateRequestDto(); dto.setTitle("A".repeat(256)); dto.setContent("Content");
        Set<ConstraintViolation<NewsCreateRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    // --- NewsUpdateRequestDto Tests ---

    @Test
    void whenUpdateDtoIsValidThenNoViolations() {
        NewsUpdateRequestDto dto = new NewsUpdateRequestDto("Updated Title", "Updated content", "Updated teaser", true, null);
        Set<ConstraintViolation<NewsUpdateRequestDto>> violations = validator.validate(dto);
        assertEquals(0, violations.size());
    }

    @Test
    void whenUpdateDtoTitleIsBlankThenViolation() {
        NewsUpdateRequestDto dto = new NewsUpdateRequestDto("", "Updated content", null, true, null);
        Set<ConstraintViolation<NewsUpdateRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenUpdateDtoTitleExceedsLimitThenViolation() {
        NewsUpdateRequestDto dto = new NewsUpdateRequestDto("A".repeat(256), "Content", null, false, null);
        Set<ConstraintViolation<NewsUpdateRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenUpdateDtoPublishedIsNullThenViolation() {
        NewsUpdateRequestDto dto = new NewsUpdateRequestDto("Title", "Content", null, null, null);
        Set<ConstraintViolation<NewsUpdateRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
