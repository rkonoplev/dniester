package com.example.phoebe.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a system user with login credentials, email, active status, and assigned roles.
 *
 * Portability and security notes:
 * - Username and email are trimmed and lowercased for consistent uniqueness across databases.
 * - Password is marked WRITE_ONLY for JSON to avoid accidental exposure in responses.
 * - The user_roles join table enforces unique pairs (user_id, role_id) to prevent duplicates.
 */
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_active", columnList = "active")
        }
)
public class User {

    /**
     * Unique identifier of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username (lowercased and trimmed).
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 64)
    @Column(unique = true, nullable = false, length = 64)
    private final String username;

    /**
     * Password hash (e.g., BCrypt/Argon2). Not exposed in JSON responses.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Password is required")
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Unique email (lowercased and trimmed).
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 254)
    @Column(unique = true, nullable = false, length = 254)
    private String email;

    /**
     * Active flag. True means the account is enabled.
     */
    @Column(nullable = false)
    private boolean active = true;

    /**
     * Roles assigned to the user.
     * LAZY to avoid fetching roles everywhere by default.
     * Unique constraint prevents duplicate links in the join table.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uq_user_roles_user_role",
                    columnNames = {"user_id", "role_id"}
            )
    )
    private Set<Role> roles = new HashSet<>();

    // === Constructors ===

    /**
     * Default constructor required by JPA.
     * The final business key 'username' is initialized to null.
     */
    public User() {
        this.username = null;
        this.password = null;
        this.email = null;
    }

    /**
     * Constructs a new User, normalizing the business key ('username') and email upon creation.
     */
    public User(String username, String password, String email, boolean active) {
        if (username != null) {
            this.username = username.trim().toLowerCase(java.util.Locale.ROOT);
        } else {
            this.username = null;
        }
        
        if (email != null) {
            this.email = email.trim().toLowerCase(java.util.Locale.ROOT);
        } else {
            this.email = null;
        }
        
        this.password = password;
        this.active = active;
    }

    // === Getters ===

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    // === Setters ===

    public void setId(Long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // === Helper Methods (bidirectional consistency, null-safe, idempotent) ===

    /**
     * Adds a role to the user and updates the inverse side.
     */
    public void addRole(Role role) {
        if (role == null) {
            return;
        }
        if (this.roles.add(role)) {
            role.getUsers().add(this);
        }
    }

    /**
     * Removes a role from the user and updates the inverse side.
     */
    public void removeRole(Role role) {
        if (role == null) {
            return;
        }
        if (this.roles.remove(role)) {
            role.getUsers().remove(this);
        }
    }

    // === equals & hashCode ===

    /**
     * Implements equality based on the business key ('username').
     * This approach is stable across all persistence states and handles proxies.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        // The business key 'username' must not be null for equality checks.
        return username != null && username.equals(user.username);
    }

    /**
     * Generates a hash code based on the business key ('username').
     * This ensures the hash code is stable before and after persistence.
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    // === toString ===

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}
