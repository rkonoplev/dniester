package com.example.newsplatform.dto;

import java.time.LocalDateTime;

public class NewsUpdateRequest {
    private String title;
    private String teaser;
    private String content;
    private LocalDateTime publishedAt;

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
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }
}
