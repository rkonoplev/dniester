package com.example.phoebe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
 * Entity representing a taxonomy term used to classify news content.
 *
 * Each term belongs to a specific vocabulary (e.g., category, tag) and can be associated with multiple news.
 *
 * Portability notes:
 * - MySQL: UNIQUE(name, vocabulary) is typically case-insensitive with default *_ci collations.
 * - PostgreSQL: if you need case-insensitive uniqueness, create a functional unique index on
 *   LOWER(name), LOWER(vocabulary) via DB migration (not expressible with JPA annotations).
 */
@Entity
@Table(
        name = "terms",
        indexes = {
                @Index(name = "idx_term_vocabulary", columnList = "vocabulary")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "vocabulary"})
        }
)
public class Term {

    /** Unique identifier of the term. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Display name of the term (e.g., Science, Sports). */
    @NotBlank(message = "Term name is required")
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String name;

    /** Vocabulary category that groups related terms (e.g., category, tag). */
    @NotBlank(message = "Vocabulary is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String vocabulary;

    /** Articles associated with this term (inverse side). */
    @ManyToMany(mappedBy = "terms", fetch = FetchType.LAZY)
    private Set<News> newsArticles = new HashSet<>();

    /** Default constructor required by JPA. */
    public Term() {
    }

    /** Constructor without id. */
    public Term(String name, String vocabulary) {
        this.name = name;
        this.vocabulary = vocabulary;
    }

    /** Constructor with id (typically used for tests). */
    public Term(Long id, String name, String vocabulary) {
        this.id = id;
        this.name = name;
        this.vocabulary = vocabulary;
    }

    /**
     * Normalizes textual fields for consistency.
     * - Trims name.
     * - Trims and lowercases vocabulary (ROOT locale).
     */
    @PrePersist
    @PreUpdate
    private void normalize() {
        if (name != null) {
            name = name.trim();
        }
        if (vocabulary != null) {
            vocabulary = vocabulary.trim().toLowerCase(Locale.ROOT);
        }
    }

    // === Getters and Setters ===

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public Set<News> getNewsArticles() {
        return newsArticles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVocabulary(String vocabulary) {
        this.vocabulary = vocabulary;
    }

    public void setNewsArticles(Set<News> newsArticles) {
        this.newsArticles = newsArticles;
    }

    // === Relationship helpers (bidirectional consistency) ===

    public void addNews(News news) {
        if (news == null) {
            return;
        }
        if (this.newsArticles.add(news)) {
            news.getTerms().add(this);
        }
    }

    public void removeNews(News news) {
        if (news == null) {
            return;
        }
        if (this.newsArticles.remove(news)) {
            news.getTerms().remove(this);
        }
    }

    // === equals & hashCode ===

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Term other)) {
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
        return "Term{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vocabulary='" + vocabulary + '\'' +
                '}';
    }
}