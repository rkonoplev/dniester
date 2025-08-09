package com.example.newsplatform.dto;

import java.time.LocalDateTime;

public class NewsDto {

    private Long id;
    private String title;
    private String teaser;
    private String content;
    private String category;
    private LocalDateTime publishedAt;
    private boolean published;

    public NewsDto() {
    }

    // Getters and Setters
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
