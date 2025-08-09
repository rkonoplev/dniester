package com.example.newsplatform.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

public class NewsUpdateRequest {

    // Fields allowed to update
    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    private String teaser;

    @NotBlank
    private String content;

    @Size(max = 100)
    private String category;

    @NotNull
    private LocalDateTime publishedAt;

    private boolean published;

    public NewsUpdateRequest() {
    }

    // Getters and Setters
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
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
    public boolean isPublished() {
        return published;
    }
    public void setPublished(boolean published) {
        this.published = published;
    }
}
