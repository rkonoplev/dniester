package com.example.phoebe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for representing a news article in API responses.
 * The teaser is optional and will not be included in the JSON if null.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewsDto {
    private Long id;
    private String title;
    private String body;
    private String teaser;
    private LocalDateTime publicationDate;
    private boolean published;
    private Long authorId;
    private String authorName;
    private Set<String> termNames;

    //<editor-fold desc="Constructors, Getters, Setters">
    public NewsDto() {}

    public NewsDto(Long id, String title, String body, String teaser,
        LocalDateTime publicationDate, boolean published, Long authorId,
        String authorName, Set<String> termNames) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.teaser = teaser;
        this.publicationDate = publicationDate;
        this.published = published;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
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