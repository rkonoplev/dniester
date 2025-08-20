package com.example.newsplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO for creating new News entries.
 */
public class NewsCreateRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    private String teaser;

    @NotBlank
    private String body;

    @NotBlank
    @Size(max = 100)
    private String category;

    @NotNull
    private LocalDateTime publicationDate;

    private boolean published;

    // --- Getters & Setters ---
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