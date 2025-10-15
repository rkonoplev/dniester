package com.example.newsplatform.entity;

import com.example.newsplatform.validation.SafeHtml;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entity representing a news article.
 * Maps to the "content" table (legacy Drupal schema).
 *
 * Portability:
 * - Large text fields use @Lob instead of vendor-specific column definitions (works on MySQL and PostgreSQL).
 * - Indexes cover common filters and sorts.
 * - Spring Data JPA Auditing populates createdAt and updatedAt.
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
 *