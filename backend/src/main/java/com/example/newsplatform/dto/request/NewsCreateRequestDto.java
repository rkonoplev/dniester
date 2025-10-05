package com.example.newsplatform.dto.request;

import com.example.newsplatform.validation.SafeHtml;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * DTO for creating a new news article.
 */
public class NewsCreateRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 50, message = "Title must not exceed 50 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @Size(max = 250, message = "Teaser must not exceed 250 characters")
    @SafeHtml(message = "Teaser contains unsafe HTML tags")
    private String teaser;

    private Set<Long> termIds;

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

    public Set<Long> getTermIds() {
        return termIds;
    }

    public void setTermIds(Set<Long> termIds) {
        this.termIds = termIds;
    }
    //</editor-fold>
}