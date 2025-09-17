package com.example.newsplatform.dto.response;

/**
 * DTO for term responses.
 */
public record TermResponseDto(
        Long id,
        String name,
        String vocabulary
) {}