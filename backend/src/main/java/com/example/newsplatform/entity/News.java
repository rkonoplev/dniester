package com.example.newsplatform.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "publication_date", nullable = false)
    private LocalDateTime publicationDate = LocalDateTime.now();

    @Column(nullable = false)
    private boolean published = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "news_terms",
            joinColumns = @JoinColumn(name = "news_id"),
            inverseJoinColumns = @JoinColumn(name = "term_id")
    )
    private Set<Term> terms = new HashSet<>();

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public boolean isPublished() {
        return published;
    }

    public User getAuthor() {
        return author;
    }

    public Set<Term> getTerms() {
        return terms;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setTerms(Set<Term> terms) {
        this.terms = terms;
    }
}