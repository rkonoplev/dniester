package com.example.newsplatform.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a news article for API input/output.
 * Used for both responses and admin operations (create/update).
 *
 * Fields:
 * - id: Unique identifier
 * - title, teaser, body: Content fields
 * - category: Display name of category (derived from Term)
 * - categoryId: ID of associated category (for updates/creation)
 * - authorId: ID of the user who authored the news
 * - publicationDate: When the article should be published
 * - published: Whether the article is publicly visible
 */
public class NewsDto {

    private Long id;

    private String title;

    private String teaser;

    /** Full article content. */
    private String body;

    /** Display name of the category (e.g., "Technology"). Derived from Term. */
    private String category;

    /** ID of the category (Term ID) used for setting/updating category. */
    private Long categoryId;

    /** ID of the user who created or authored this news. */
    private Long authorId;

    /** Scheduled publication date and time. */
    private LocalDateTime publicationDate;

    /** Flag indicating whether the news is published and visible to public. */
    private boolean published;

    // === Constructors ===

    public NewsDto() {}

    public NewsDto(Long id, String title, String teaser, String body,
                   String category, LocalDateTime publicationDate, boolean published) {
        this.id = id;
        this.title = title;
        this.teaser = teaser;
        this.body = body;
        this.category = category;
        this.publicationDate = publicationDate;
        this.published = published;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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