package com.example.newsplatform.dto;

/**
 * DTO for Role API responses.
 */
public record RoleDto(
        Long id,
        String name,
        int userCount
) {}