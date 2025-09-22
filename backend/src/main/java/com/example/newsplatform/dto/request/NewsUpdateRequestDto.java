package com.example.newsplatform.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for updating an existing news article.
 * All fields are optional — only provided values should be updated.
 *
 * Note: Use wrapper types (e.g., Boolean) to distinguish between
 * "not provided" and "explicit value".
 */
public class NewsUpdateRequestDto {

    @Size(max = 255)
    private String title;

    @Size(max = 500)
    private String teaser;

    private String body;

    @Size(max = 100)
    private String category;

    private Long categoryId;

    private Long authorId;

    private LocalDateTime publicationDate;

    private Boolean published;

    // === NEW FIELD ===
    // Set of term IDs to associate with the news article.
    private Set<Long> termIds;

    // === Getters and Setters ===

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTeaser() { return teaser; }
    public void setTeaser(String teaser) { this.teaser = teaser; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public LocalDateTime getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDateTime publicationDate) { this.publicationDate = publicationDate; }

    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }

    // === NEW GETTER/SETTER ===
    public Set<Long> getTermIds() {
        return termIds;
    }

    public void setTermIds(Set<Long> termIds) {
        this.termIds = termIds;
    }
}