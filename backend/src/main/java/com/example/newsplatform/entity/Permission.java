package com.example.newsplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

/**
 * Permission entity representing granular system permissions.
 * Examples: CREATE_NEWS, EDIT_NEWS, DELETE_NEWS, MANAGE_USERS, etc.
 */
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Permission name is required")
    @Size(max = 100, message = "Permission name must not exceed 100 characters")
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(length = 255)
    private String description;

    @Size(max = 50, message = "Resource must not exceed 50 characters")
    @Column(length = 50)
    private String resource; // e.g., "NEWS", "USERS", "TERMS"

    @Size(max = 50, message = "Action must not exceed 50 characters")
    @Column(length = 50)
    private String action; // e.g., "CREATE", "READ", "UPDATE", "DELETE"

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    // Constructors
    public Permission() {}

    public Permission(String name, String description, String resource, String action) {
        this.name = name;
        this.description = description;
        this.resource = resource;
        this.action = action;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}