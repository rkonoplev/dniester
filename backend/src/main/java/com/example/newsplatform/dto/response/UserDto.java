package com.example.newsplatform.dto.response;

import java.util.Set;

/**
 * DTO for User API responses.
 */
public record UserDto(
        Long id,
        String username,
        String email,
        boolean active,
        Set<String> roleNames
) {}