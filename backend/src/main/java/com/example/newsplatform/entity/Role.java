package com.example.newsplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Represents a user role (e.g., ADMIN, EDITOR) used for authorization in the system.
 *
 * Portability notes:
 * - Names are normalized to UPPER_CASE for consistency across databases.
 * - For the join table role_permissions, a unique pair (role_id, permission_id) is enforced via @JoinTable.
 * - Consider adding DB indexes on role_permissions(role_id) and role_permissions(permission_id) in migrations.
 */
@Entity
@Table(name = "roles")
public class Role {

    /**
     * Unique identifier of the role.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Role name (e.g., ADMIN, EDITOR, USER). Stored in UPPER_CASE.
     */
    @NotBlank(message = "Role name is required")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Optional description of the role.
     */
    @Size(max = 255)
    @Column(length = 255)
    private String description;

    /**
     * Users who have this role (inverse side of User.roles).
     * Kept LAZY to avoid unnecessary loading.
     */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    /**
     * Permissions associated with this role.
     * LAZY to avoid fetching permissions everywhere by default.
     * Unique constraint prevents duplicate links in the join table.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uq_role_permissions_role_perm",
                    columnNames = {"role_id", "permission_id"}
            )
    )
    private Set<Permission> permissions = new HashSet<>();

    // === Constructors ===

    public Role() {
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // === Normalization ===

    /**
     * Normalizes role name to UPPER_CASE and trims surrounding spaces.
     */
    @PrePersist
    @PreUpdate
    private void normalize() {
        if (name != null) {
            name = name.trim().toUpperCase(Locale.ROOT);
        }
        if (description != null) {
            description = description.trim();
        }
    }

    // === Getters and Setters ===

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public String getDescription() {
        return description;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    // === Helper Methods (bidirectional consistency, null-safe, idempotent) ===

    public void addUser(User user) {
        if (user == null) {
            return;
        }
        if (this.users.add(user)) {
            user.getRoles().add(this);
        }
    }

    public void removeUser(User user) {
        if (user == null) {
            return;
        }
        if (this.users.remove(user)) {
            user.getRoles().remove(this);
        }
    }

    public void addPermission(Permission permission) {
        if (permission == null) {
            return;
        }
        if (this.permissions.add(permission)) {
            permission.getRoles().add(this);
        }
    }

    public void removePermission(Permission permission) {
        if (permission == null) {
            return;
        }
        if (this.permissions.remove(permission)) {
            permission.getRoles().remove(this);
        }
    }

    // === equals & hashCode ===
    // Proxy-friendly equals and stable hash: class-hash when id is null, id-hash after persistence.

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role other)) {
            return false;
        }
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }
        return java.util.Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : java.util.Objects.hash(name);
    }

    // === toString ===

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}