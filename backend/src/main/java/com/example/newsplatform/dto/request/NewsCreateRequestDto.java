package com.example.newsplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new news article.
 */
public class NewsCreateRequestDto {

    @NotBlank
    @Size(min = 3, max = 255)
    private String title;

    @NotBlank
    private String content;

    // Getters and Setters
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
}