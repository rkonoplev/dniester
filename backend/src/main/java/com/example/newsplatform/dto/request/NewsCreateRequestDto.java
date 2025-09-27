package com.example.newsplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new news article.
 */
public class NewsCreateRequestDto {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255)
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String teaser;

    //<editor-fold desc="Getters and Setters">
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }
    //</editor-fold>
}