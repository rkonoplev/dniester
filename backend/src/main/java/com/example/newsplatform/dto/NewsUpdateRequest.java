package com.example.newsplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO for updating existing news entries.
 */
public class NewsUpdateRequest {
    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    private String teaser;

    @NotBlank
    private String content;

    @NotNull
    private LocalDateTime publishedAt;

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTeaser() { return teaser; }
    public void setTeaser(String teaser) { this.teaser = teaser; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}
