package com.example.newsplatform.dto;

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
        Set<String> termNames
) {}