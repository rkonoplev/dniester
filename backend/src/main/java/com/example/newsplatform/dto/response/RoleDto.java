package com.example.newsplatform.dto.response;

import java.util.Set;

/**
 * DTO for Role API responses with permissions.
 */
public record RoleDto(
        Long id,
        String name,
        String description,
        boolean active,
        int userCount,
        Set<PermissionDto> permissions
) {}