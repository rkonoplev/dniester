package com.example.newsplatform.entity;

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
import java.util.Set;

/**
 * Represents a specific permission in the system (e.g., "news:create", "users:delete").
 * Permissions are assigned to roles to grant granular access control.
 *
 * Notes:
 * - Name is normalized to lowercase and trimmed for consistent uniqueness across databases.
 * - Equality is id-based to be proxy-friendly and safe for JPA collections.
 */
@Entity
@Table(name = "permissions")
public class Permission {

    /**
     * Unique identifier of the permission.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique permission name (e.g., "news:create").
     */
    @NotBlank(message = "Permission name is required")
    @Size(max = 100)
    @Column(unique = true, nullable = false, length = 100)
    private String name;

    /**
     * Roles that include this permission (inverse side).
     * The owning side is Role.permissions.
     */
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    // === Constructors ===

    public Permission() {
    }

    public Permission(String name) {
        this.name = name;
    }

    // === Normalization ===

    /**
     * Normalizes the name by trimming and lowercasing (ROOT).
     */
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // === Helper methods (bidirectional consistency, null-safe, idempotent) ===

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
    // Proxy-friendly equals and stable hash: class-hash when id is null, id-hash after persistence.

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Permission other)) {
            return false;
        }
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return (id == null) ? getClass().hashCode() : id.hashCode();
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
