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
    private String name;

    /** A human-readable description of what the permission allows. */
    @Size(max = 255)
    @Column(length = 255)
    private String description;

    /** Roles that include this permission (inverse side). */
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    // === Constructors ===

    public Permission() {
    }

    public Permission(String name) {
        this.name = name;
    }

    // === Normalization ===

    /** Normalizes the name by trimming and lowercasing (ROOT). */
    @PrePersist
    @PreUpdate
    private void normalize() {
        if (name != null) {
            name = name.trim().toLowerCase(Locale.ROOT);
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

    public void setName(String name) {
        this.name = name;
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
    // Proxy-friendly; falls back to name when id is null.

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permission other)) {
            return false;
        }
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : Objects.hash(name);
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