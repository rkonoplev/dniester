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
    private final String name;

    /** Vocabulary category that groups related terms (e.g., category, tag). */
    @NotBlank(message = "Vocabulary is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private final String vocabulary;

    /** Articles associated with this term (inverse side). */
    @ManyToMany(mappedBy = "terms", fetch = FetchType.LAZY)
    private Set<News> newsArticles = new HashSet<>();

    /** Default constructor required by JPA. */
    public Term() {
        // Business keys are null in the default constructor.
        this.name = null;
        this.vocabulary = null;
    }

    /** Constructor with business key. */
    public Term(String name, String vocabulary) {
        // Normalize and assign final fields directly in the constructor
        if (name != null) {
            this.name = name.trim();
        } else {
            this.name = null;
        }

        if (vocabulary != null) {
            this.vocabulary = vocabulary.trim().toLowerCase(Locale.ROOT);
        } else {
            this.vocabulary = null;
        }
    }

    /**
     * Constructor with id, typically used for test data setup.
     * Note: 'id' is normally set by the persistence provider.
     */
    public Term(Long id, String name, String vocabulary) {
        this.id = id;
        this.name = name;
        this.vocabulary = vocabulary;
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

    /**
     * Implements equality based on the composite business key ('name', 'vocabulary').
     * This approach is stable across all persistence states.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Term term = (Term) o;
        // Business keys must not be null for equality checks.
        return name != null && vocabulary != null &&
                name.equals(term.name) &&
                vocabulary.equals(term.vocabulary);
    }

    /**
     * Generates a hash code based on the composite business key ('name', 'vocabulary').
     * This ensures the hash code is stable before and after persistence.
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, vocabulary);
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