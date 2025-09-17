package com.example.newsplatform.dto.response;

/**
 * DTO for Role API responses.
 */
public record RoleDto(
        Long id,
        String name,
        int userCount
) {}