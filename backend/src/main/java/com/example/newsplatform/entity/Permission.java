package com.example.newsplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Represents a specific permission in the system (e.g., "news:create", "users:delete").
 * Permissions are assigned to roles to grant granular access control.
 */
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique name of the permission, following a "resource:action" convention.
     * Example: "news:read", "news:write", "users:manage".
     */
    @Column(unique = true, nullable = false, length = 100)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Hook method called before entity update.
     * Can be overridden in tests or extended for audit logic.
     */
    protected void onUpdate() {
        // No-op by default
    }

    /**
     * JPA lifecycle callback that triggers before entity update.
     * Delegates to onUpdate() for testability and extensibility.
     */
    @PreUpdate
    private void preUpdate() {
        onUpdate();
    }

    /**
     * Equality is based on ID if both entities are persisted.
     * If not persisted, falls back to comparing unique name.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;

        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }

        return Objects.equals(name, that.name);
    }

    /**
     * Hash code is based on ID if available, otherwise on name.
     * Ensures consistency with equals() implementation.
     */
    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : Objects.hash(name);
    }

    /**
     * String representation for logging and debugging.
     */
    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}