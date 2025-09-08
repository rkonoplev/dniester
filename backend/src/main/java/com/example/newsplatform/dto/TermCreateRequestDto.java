package com.example.newsplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating new terms.
 */
public record TermCreateRequestDto(
        @NotBlank(message = "Term name is required")
        @Size(max = 100, message = "Term name must not exceed 100 characters")
        String name,
        
        @NotBlank(message = "Vocabulary is required")
        @Size(max = 50, message = "Vocabulary must not exceed 50 characters")
        String vocabulary
) {}