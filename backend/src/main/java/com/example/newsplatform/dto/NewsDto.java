package com.example.newsplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class NewsDto {
    private Long id;

    @NotBlank
    private String title;

    private String teaser;

    @NotBlank
    private String content;

    @NotBlank
    @Size(max = 100)
    private String category;
    private LocalDateTime publishedAt;

    public NewsDto(Long id, String title, String teaser, String content, String category, LocalDateTime publishedAt) {
        this.id = id;
        this.title = title;
        this.teaser = teaser;
        this.content = content;
        this.category = category;
        this.publishedAt = publishedAt;
    }

    public NewsDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTeaser() { return teaser; }
    public void setTeaser(String teaser) { this.teaser = teaser; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}