package com.example.newsplatform.dto;

import java.time.LocalDateTime;

/**
 * DTO representing News entity for API responses.
 */
public class NewsDto {

    private Long id;
    private String title;
    private String teaser;

    /** Main article body. */
    private String body;

    /** Category name (maps to taxonomy Term name). */
    private String category;

    /** Publication date. */
    private LocalDateTime publicationDate;

    /** Whether this news is published. */
    private boolean published;

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

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTeaser() { return teaser; }
    public void setTeaser(String teaser) { this.teaser = teaser; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDateTime publicationDate) { this.publicationDate = publicationDate; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}