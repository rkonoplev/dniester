package com.example.newsplatform.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for returning news data to clients.
 */
public class NewsDto {
    private Long id;
    private String title;
    private String teaser;
    private String content;
    private String category;
    private LocalDateTime publishedAt;

    // All-args constructor for mapping
    public NewsDto(Long id, String title, String teaser, String content, String category, LocalDateTime publishedAt) {
        this.id = id;
        this.title = title;
        this.teaser = teaser;
        this.content = content;
        this.content = category;
        this.publishedAt = publishedAt;
    }

    // No-args constructor for frameworks
    public NewsDto() {
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTeaser() { return teaser; }
    public void setTeaser(String teaser) { this.teaser = teaser; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return content; }
    public void setCategory(String content) { this.content = content; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}
