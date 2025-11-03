package com.example.phoebe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;

import java.util.Objects;
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
     * Role name (e.g., ADMIN, EDITOR). Stored in UPPER_CASE.
     */
    @NotBlank(message = "Role name is required")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private final String name;

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

    /**
     * Default constructor required by JPA.
     * The final business key 'name' is initialized to null.
     */
    public Role() {
        this.name = null;
        this.description = null;
    }

    /**
     * Constructs a new Role, normalizing the business key ('name') upon creation.
     */
    public Role(String name, String description) {
        if (name != null) {
            this.name = name.trim().toUpperCase(java.util.Locale.ROOT);
        } else {
            this.name = null;
        }
        this.description = description;
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
        Role role = (Role) o;
        // The business key 'name' must not be null for equality checks.
        return name != null && name.equals(role.name);
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
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}