package com.example.newsplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO for creating a new news article.
 *
 * Fields:
 * - title, body: Required content
 * - teaser: Optional preview text
 * - categoryId: ID of the associated category (Term ID)
 * - authorId: ID of the user creating the article
 * - publicationDate: When the article should be published
 * - published: Whether the article is immediately visible
 *
 * Note: All fields are required unless marked otherwise.
 */
public class NewsCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @Size(max = 500)
    private String teaser;

    @NotBlank(message = "Body is required")
    private String body;

    /**
     * ID of the category (Term ID). Required to associate news with a taxonomy.
     * Replaces string-based 'category' for consistency and data integrity.
     */
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    /**
     * ID of the author (User ID). Required to assign ownership.
     */
    @NotNull(message = "Author ID is required")
    private Long authorId;

    /**
     * Scheduled publication date and time.
     */
    @NotNull(message = "Publication date is required")
    private LocalDateTime publicationDate;

    /**
     * Whether the article should be published immediately.
     * Unlike update, this is required (cannot be null).
     */
    private boolean published;

    // === Getters and Setters ===

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
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
}