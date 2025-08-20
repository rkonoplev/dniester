package com.example.newsplatform.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Role entity representing user roles (e.g., ADMIN, EDITOR).
 */
@Entity
@Table(name = "roles")
public class Role {

    @Id
    private Long id; // Drupal rid

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }
}