package com.example.newsplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Role entity representing user roles with associated permissions.
 * Modern RBAC implementation with granular permission control.
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    private Long id; // Drupal rid

    @NotBlank(message = "Role name is required")
    @Size(max = 100, message = "Role name must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }

    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }

    /**
     * Check if this role has a specific permission.
     */
    public boolean hasPermission(String permissionName) {
        return permissions.stream()
                .anyMatch(permission -> permission.getName().equals(permissionName));
    }

    /**
     * Add permission to this role.
     */
    public void addPermission(Permission permission) {
        permissions.add(permission);
        permission.getRoles().add(this);
    }

    /**
     * Remove permission from this role.
     */
    public void removePermission(Permission permission) {
        permissions.remove(permission);
        permission.getRoles().remove(this);
    }
}