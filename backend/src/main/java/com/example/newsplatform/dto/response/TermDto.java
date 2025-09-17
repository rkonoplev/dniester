package com.example.newsplatform.dto.response;

/**
 * DTO for Term API responses.
 */
public record TermDto(
        Long id,
        String name,
        String vocabulary,
        int newsCount
) {}