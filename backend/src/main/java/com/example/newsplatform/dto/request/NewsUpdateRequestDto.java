package com.example.newsplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing news article.
 */
public record NewsUpdateRequestDto(
        @NotBlank @Size(min = 3, max = 255)
        String title,
        @NotBlank
        String content,
        @NotNull
        Boolean isPublished
) {}