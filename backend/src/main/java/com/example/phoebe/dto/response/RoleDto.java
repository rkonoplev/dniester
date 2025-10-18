package com.example.phoebe.dto.response;

import java.util.Set;

/**
 * DTO for representing a Role in API responses.
 * It provides a client-friendly view of a role, including its name,
 * description, and the names of the permissions it grants.
 *
 * @param id The unique identifier of the role.
 * @param name The name of the role (e.g., "ADMIN").
 * @param description A brief description of the role's purpose.
 * @param permissionNames A set of strings representing the names of permissions granted to this role.
 */
public record RoleDto(
        Long id,
        String name,
        String description,
        Set<String> permissionNames
) {}