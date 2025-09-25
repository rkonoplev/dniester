package com.example.newsplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new Term (category or tag).
 */
public record TermCreateRequestDto(
        @NotBlank(message = "Term name is required")
        @Size(max = 255, message = "Term name must not exceed 255 characters")
        String name,

        @Size(max = 100, message = "Vocabulary must not exceed 100 characters")
        String vocabulary
) {}