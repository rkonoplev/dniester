package com.example.newsplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entity representing a news article.
 * Maps to the "content" database table (legacy Drupal schema).
 *
 * Contains core article data, publication status, timestamps, author reference,
 * and taxonomy terms (categories/tags) via many-to-many relationship.
 */
@Entity
@Table(
        name = "content",
        indexes = {
                @Index(name = "idx_news_title", columnList = "title"),
                @Index(name = "idx_news_published", columnList = "published"),
                @Index(name = "idx_news_publication_date", columnList = "publication_date")
        }
)
public class News {

    /**
     * Unique identifier for the news article.
     * Corresponds to the original Drupal node ID (nid).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Article title. Must not be null.
     */
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * Main article body (full text). Stored as TEXT in DB.
     */
    @Column(columnDefinition = "TEXT")
    private String body;

    /**
     * Short preview or lead text shown in lists. Optional.
     */
    @Column(columnDefinition = "TEXT")
    private String teaser;

    /**
     * Scheduled or actual publication date and time.
     * Corresponds to original "created" timestamp in Drupal.
     */
    @Column(name = "publication_date", nullable = false)
    private LocalDateTime publicationDate;

    /**
     * Flag indicating whether the article is publicly visible.
     * Default: false (draft).
     */
    @Column(nullable = false)
    private boolean published = false;

    // === Audit Fields ===

    /**
     * Timestamp when the record was first created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the record was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * Version field for optimistic locking.
     */
    @Version
    private Long version;

    // === Relationships ===

    /**
     * Author of the news article (User who created it).
     * Foreign key: author_id → User.id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * Taxonomy terms associated with this news (e.g., categories, tags).
     * Stored in join table "content_terms".
     *
     * Example: ["Technology", "Urgent"]
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "content_terms",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "term_id")
    )
    private Set<Term> terms = new HashSet<>();

    // === Lifecycle Callbacks ===

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (publicationDate == null) {
            publicationDate = now;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // === Getters and Setters ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<Term> getTerms() {
        return terms;
    }

    public void setTerms(Set<Term> terms) {
        this.terms = terms;
    }

    public Long getVersion() {
        return version;
    }

    // === equals & hashCode ===

    /**
     * Compares News entities by ID only (database identity).
     * Important for JPA consistency and collection handling.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News news)) return false;
        return Objects.equals(id, news.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // === toString ===

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", publicationDate=" + publicationDate +
                ", author=" + (author != null ? author.getId() : "null") +
                '}';
    }
}
