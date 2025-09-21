package com.example.newsplatform.dto.response;

/**
 * DTO for Permission entity responses.
 */
public record PermissionDto(
        Long id,
        String name,
        String description,
        String resource,
        String action
) {}