package com.example.newsplatform.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Term entity representing taxonomy terms (categories/tags).
 */
@Entity
@Table(name = "terms")
public class Term {

    @Id
    private Long id; // Drupal tid

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 100)
    private String vocabulary;

    @ManyToMany(mappedBy = "terms")
    private Set<News> newsArticles = new HashSet<>();

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVocabulary() { return vocabulary; }
    public void setVocabulary(String vocabulary) { this.vocabulary = vocabulary; }

    public Set<News> getNewsArticles() { return newsArticles; }
    public void setNewsArticles(Set<News> newsArticles) { this.newsArticles = newsArticles; }
}
