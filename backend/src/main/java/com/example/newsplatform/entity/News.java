package com.example.newsplatform.entity;

import com.example.newsplatform.validation.SafeHtml;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entity representing a news article.
 * Maps to the "content" table (legacy Drupal schema).
 *
 * The model is designed to work well with both MySQL and PostgreSQL:
 * - Large text fields use @Lob instead of vendor-specific columnDefinition.
 * - Indexes are defined for common filters and sorts.
 * - Auditing is enabled for createdAt and updatedAt.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "content",
        indexes = {
                @Index(name = "idx_news_title", columnList = "title"),
                @Index(name = "idx_news_published", columnList = "published"),
                @Index(name = "idx_news_publication_date", columnList = "publication_date"),
                @Index(name = "idx_news_author", columnList = "author_id"),
                @Index(name = "idx_news_published_pubdate", columnList = "published, publication_date")
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
    @Size(max = 50, message = "Title must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String title;

    /**
     * Main article body (full text).
     * Stored as LOB for portability across MySQL and PostgreSQL.
     * Lazy loading for performance. Lazy on basic types requires bytecode
     * enhancement in Hibernate to be truly lazy.
     */
    @SafeHtml(message = "Body contains unsafe HTML content")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String body;

    /**
     * Short teaser or lead text for lists.
     * Stored as LOB for portability. Limited by validation to 250 characters.
     * Lazy loading to avoid pulling large text into listings.
     */
    @SafeHtml(message = "Teaser contains unsafe HTML content")
    @Size(max = 250, message = "Teaser must not exceed 250 characters")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String teaser;

    /**
     * Scheduled or actual publication date and time.
     * Defaults to current timestamp on create if not provided.
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
     * Managed by Spring Data JPA Auditing.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the record was last updated.
     * Managed by Spring Data JPA Auditing.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Version field for optimistic locking.
     */
    @Version
    private Long version;

    // === Relationships ===

    /**
     * Author of the news article.
     * Foreign key: author_id -> users.id
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * Taxonomy terms associated with this news (categories, tags).
     * Stored in join table "content_terms".
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "content_terms",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "term_id")
    )
    @BatchSize(size = 50)
    private Set<Term> terms = new HashSet<>();

    // === Lifecycle Callback ===

    /**
     * Ensures publicationDate is set on create if it is not provided.
     * Auditing will populate createdAt and updatedAt automatically.
     */
    @PrePersist
    public void onCreate() {
        if (publicationDate == null) {
            publicationDate = LocalDateTime.now();
        }
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
     * Equality is based on non-null identifier. This is proxy-friendly and
     * safe for JPA collections.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof News other)) {
            return false;
        }
        return id != null && id.equals(other.getId());
    }

    /**
     * Hash code is stable for transient instances and becomes id-based after
     * persistence.
     */
    @Override
    public int hashCode() {
        return (id == null) ? getClass().hashCode() : id.hashCode();
    }

    // === toString ===

    /**
     * Safe string representation for logs and debugging.
     * Does not trigger loading of lazy collections.
     */
    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", publicationDate=" + publicationDate +
                ", author=" + (author != null ? author.getId() : null) +
                '}';
    }
}