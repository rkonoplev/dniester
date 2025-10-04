package com.example.newsplatform.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * DTO for updating an existing news article.
 * All fields are optional to support partial updates (PATCH-style).
 */
public record NewsUpdateRequestDto(
        @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
        String title,

        // This field maps to the 'body' of the News entity
        String content,

        // This field maps to the 'teaser' of the News entity
        String teaser,

        @NotNull(message = "Publication status must be provided")
        Boolean isPublished,

        Set<Long> termIds
) {}