package com.example.newsplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

/**
 * DTO for creating new roles with permissions.
 */
public class RoleCreateRequestDto {

    @NotBlank(message = "Role name is required")
    @Size(max = 100, message = "Role name must not exceed 100 characters")
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private boolean active = true;

    private Set<Long> permissionIds;

    // Constructors
    public RoleCreateRequestDto() {}

    public RoleCreateRequestDto(String name, String description, boolean active, Set<Long> permissionIds) {
        this.name = name;
        this.description = description;
        this.active = active;
        this.permissionIds = permissionIds;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Set<Long> getPermissionIds() { return permissionIds; }
    public void setPermissionIds(Set<Long> permissionIds) { this.permissionIds = permissionIds; }
}