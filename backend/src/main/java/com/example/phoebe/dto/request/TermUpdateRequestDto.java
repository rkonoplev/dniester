package com.example.phoebe.dto.request;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing Term.
 * All fields are optional; only provided fields will be updated.
 */
public record TermUpdateRequestDto(
        @Size(max = 255, message = "Term name must not exceed 255 characters")
        String name,

        @Size(max = 100, message = "Vocabulary must not exceed 100 characters")
        String vocabulary
) {}