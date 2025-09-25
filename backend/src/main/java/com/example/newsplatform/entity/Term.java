package com.example.newsplatform.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing a taxonomy term, such as a category or tag.
 * Terms are used to classify content (e.g., News articles) and grouped by vocabulary.
 *
 * Example:
 * - Name: "Technology", Vocabulary: "category"
 * - Name: "Urgent", Vocabulary: "tag"
 *
 * This entity is linked to News via a many-to-many relationship.
 * The owning side is {@link News#terms}, so this side uses mappedBy.
 */
@Entity
@Table(name = "terms",
        indexes = @Index(name = "idx_term_name", columnList = "name"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "vocabulary"}))
public class Term {

    /**
     * Unique identifier for the term.
     * May correspond to external system IDs (e.g., Drupal tid).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Display name of the term (e.g., "Sports", "Breaking News").
     * Must not be null.
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * Grouping namespace for terms (e.g., 'category', 'tag').
     * Helps organize terms into vocabularies.
     */
    @Column(length = 100)
    private String vocabulary;

    /**
     * Bidirectional many-to-many relationship with News.
     * This is the inverse (mapped) side — the owning side is in {@link News}.
     *
     * Contains all news articles associated with this term.
     */
    @ManyToMany(mappedBy = "terms", fetch = FetchType.LAZY)
    private Set<News> newsArticles = new HashSet<>();

    // === Constructors ===

    public Term() {
    }

    public Term(Long id, String name, String vocabulary) {
        this.id = id;
        this.name = name;
        this.vocabulary = vocabulary;
    }

    // === Getters and Setters ===

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

    public String getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(String vocabulary) {
        this.vocabulary = vocabulary;
    }

    public Set<News> getNewsArticles() {
        return newsArticles;
    }

    public void setNewsArticles(Set<News> newsArticles) {
        this.newsArticles = newsArticles;
    }

    // === equals & hashCode ===

    /**
     * Compares terms by identity (ID and vocabulary).
     * Useful for collections and JPA consistency.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return id != null && Objects.equals(id, term.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // === toString ===

    @Override
    public String toString() {
        return "Term{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vocabulary='" + vocabulary + '\'' +
                '}';
    }
}