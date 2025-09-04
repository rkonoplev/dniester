package com.example.newsplatform.value;

/**
 * Value object for author information.
 */
public record AuthorInfo(
        Long id,
        String username,
        String displayName
) {}