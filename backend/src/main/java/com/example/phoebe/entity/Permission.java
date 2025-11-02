package com.example.phoebe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a specific permission in the system (e.g., "news:create", "users:delete").
 * Permissions are assigned to roles to grant granular access control.
 *
 * Notes:
 * - The name is normalized to lowercase and trimmed for consistent uniqueness.
 * - Equality is based on id when available, otherwise on name.
 */
@Entity
@Table(name = "permissions")
public class Permission {

    /** Unique identifier of the permission. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique permission name (e.g., "news:create"). */
    @NotBlank(message = "Permission name is required")
    @Size(max = 100)
    @Column(unique = true, nullable = false, length = 100)
    private final String name;

    /** A human-readable description of what the permission allows. */
    @Size(max = 255)
    @Column(length = 255)
    private String description;

    /** Roles that include this permission (inverse side). */
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    // === Constructors ===

    /**
     * Default constructor required by JPA.
     * The final business key 'name' is initialized to null.
     */
    public Permission() {
        this.name = null;
        this.description = null;
    }

    /**
     * Constructs a new Permission, normalizing the business key ('name') upon creation.
     */
    public Permission(String name) {
        if (name != null) {
            this.name = name.trim().toLowerCase(java.util.Locale.ROOT);
        } else {
            this.name = null;
        }
    }

    // === Getters & Setters ===

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // === Helper methods (bidirectional consistency, null-safe) ===

    public void addRole(Role role) {
        if (role == null) {
            return;
        }
        if (this.roles.add(role)) {
            role.getPermissions().add(this);
        }
    }

    public void removeRole(Role role) {
        if (role == null) {
            return;
        }
        if (this.roles.remove(role)) {
            role.getPermissions().remove(this);
        }
    }

    // === equals & hashCode ===

    /**
     * Implements equality based on the business key ('name').
     * This approach is stable across all persistence states (transient, managed, detached).
     * It also correctly handles Hibernate proxies.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Permission that = (Permission) o;
        // The business key 'name' must not be null for equality checks.
        return name != null && name.equals(that.name);
    }

    /**
     * Generates a hash code based on the business key ('name').
     * This ensures the hash code is stable before and after persistence.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    // === toString ===

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}