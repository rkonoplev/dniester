package com.example.newsplatform.dto;

/**
 * DTO for term responses.
 */
public record TermResponseDto(
        Long id,
        String name,
        String vocabulary
) {}