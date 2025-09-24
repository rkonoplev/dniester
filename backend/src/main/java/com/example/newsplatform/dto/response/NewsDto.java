package com.example.newsplatform.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for representing a news article in API responses.
 */
public class NewsDto {
    private Long id;
    private String title;
    private String content;
    private boolean published;
    private LocalDateTime publicationDate;
    private Long authorId;
    private String authorName;
    private Set<String> termNames;

    //<editor-fold desc="Constructors, Getters, Setters">
    public NewsDto() {
    }

    public NewsDto(Long id, String title, String content, boolean published, LocalDateTime publicationDate, Long authorId, String authorName, Set<String> termNames) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.published = published;
        this.publicationDate = publicationDate;
        this.authorId = authorId;
        this.authorName = authorName;
        this.termNames = termNames;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Set<String> getTermNames() {
        return termNames;
    }

    public void setTermNames(Set<String> termNames) {
        this.termNames = termNames;
    }
    //</editor-fold>
}