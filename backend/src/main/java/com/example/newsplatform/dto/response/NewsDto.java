package com.example.newsplatform.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for News API responses.
 */
public record NewsDto(
        Long id,
        String title,
        String body,
        String teaser,
        LocalDateTime publicationDate,
        boolean published,
        String authorName,
        Set<String> termNames,
        String category,
        Long categoryId,
        Long authorId
) {}