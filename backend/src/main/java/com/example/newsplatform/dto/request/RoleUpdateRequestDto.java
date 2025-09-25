package com.example.newsplatform.dto.request;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing role.
 * Fields are optional to support partial updates (PATCH-style).
 */
public record RoleUpdateRequestDto(
        @Size(max = 50, message = "Role name must not exceed 50 characters")
        String name,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description
) {}