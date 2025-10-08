package com.example.newsplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing a taxonomy term used to classify news content.
 *
 * Each term belongs to a specific vocabulary (for example: "category" or "tag")
 * and can be associated with multiple news articles.
 *
 * Examples:
 * - Vocabulary "category": terms "Politics", "Technology"
 * - Vocabulary "tag": terms "Breaking", "Exclusive"
 *
 * This entity corresponds conceptually to Drupal’s taxonomy term.
 */
@Entity
@Table(
        name = "terms",
        indexes = {
                @Index(name = "idx_term_name", columnList = "name")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "vocabulary"})
        }
)
public class Term {

    /**
     * Unique identifier of the term.
     * May correspond to a legacy taxonomy ID if migrated from another system.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Display name of the term (e.g. "Science", "Sports").
     * Must not be blank.
     */
    @NotBlank(message = "Term name is required")
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * Vocabulary that groups related terms.
     * For example: "category", "tag", or "region".
     */
    @NotBlank(message = "Vocabulary is required")
    @Column(nullable = false, length = 100)
    private String vocabulary;

    /**
     * Articles associated with this term.
     *
     * This side is the inverse of the many-to-many relation.
     * The owning side is defined in News.terms.
     */
    @ManyToMany(mappedBy = "terms", fetch = FetchType.LAZY)
    private Set<News> newsArticles = new HashSet<>();

    /** Default constructor required by JPA. */
    public Term() {
    }

    /**
     * Constructs a Term with the specified values.
     *
     * @param id          identifier (nullable before persistence)
     * @param name        term name
     * @param vocabulary  vocabulary name
     */
    public Term(Long id, String name, String vocabulary) {
        this.id = id;
        this.name = name;
        this.vocabulary = vocabulary;
    }

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

    /**
     * Equality is based on the entity identifier.
     * Two terms are considered equal if their non-null IDs are equal.
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
        return id != null && Objects.equals(id, term.id);
    }

    /**
     * Returns a constant hash code based on the entity class.
     * Recommended pattern for JPA entities with generated identifiers.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Returns a readable string representation for debugging and logs.
     */
    @Override
    public String toString() {
        return "Term{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vocabulary='" + vocabulary + '\'' +
                '}';
    }
}
