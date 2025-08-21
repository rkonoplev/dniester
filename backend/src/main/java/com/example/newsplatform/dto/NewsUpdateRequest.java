package com.example.newsplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO for updating an existing news article.
 * All fields are optional — only provided values should be updated.
 *
 * Note: Use wrapper types (e.g., Boolean) to distinguish between "not provided" and "explicit value".
 */
public class NewsUpdateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @Size(max = 500)
    private String teaser;

    @NotBlank(message = "Body is required")
    private String body;

    /** Optional category name. */
    @Size(max = 100)
    private String category;

    /** New category ID (Term ID) if category is being changed. */
    private Long categoryId;

    /** ID of the author (User ID) if reassigning authorship. */
    private Long authorId;

    /** Scheduled publication date. */
    @NotNull(message = "Publication date is required")
    private LocalDateTime publicationDate;

    /** Whether the article should be published. Use Boolean to allow null check. */
    private Boolean published;

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

    public Boolean isPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }
}