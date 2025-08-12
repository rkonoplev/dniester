package com.example.newsplatform.dto;

import java.time.LocalDateTime;

public record NewsDto(
        Long id,
        String title,
        String teaser,
        String content,
        LocalDateTime publishedAt
) {}
