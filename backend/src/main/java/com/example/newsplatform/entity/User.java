package com.example.newsplatform.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User entity representing system users (migrated from Drupal 6 or newly created).
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    // For migrated users we preserve original Drupal uid.
    // For newly created users we can enable Generation if required:
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false)
    private boolean active; // true=active, false=blocked

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<News> newsArticles = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Set<News> getNewsArticles() { return newsArticles; }
    public void setNewsArticles(Set<News> newsArticles) { this.newsArticles = newsArticles; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}