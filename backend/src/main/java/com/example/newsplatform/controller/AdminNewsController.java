package com.example.newsplatform.controller;

import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Admin controller for managing news articles.
 * Accessible only to authenticated administrators.
 */
@RestController
@RequestMapping("/api/admin/news")
public class AdminNewsController {

    private final NewsService newsService;

    @Autowired
    public AdminNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Search all news (published and unpublished) by keyword and/or category.
     * Search is case-insensitive and looks in both title and content.
     */
    @GetMapping
    public Page<NewsDto> searchAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        return newsService.searchAll(search, category, pageable);
    }

    /**
     * Create a new news article.
     */
    @PostMapping
    public NewsDto create(@RequestBody NewsDto newsDto) {
        return newsService.create(newsDto);
    }

    /**
     * Update an existing news article.
     */
    @PutMapping("/{id}")
    public NewsDto update(@PathVariable Long id, @RequestBody NewsDto newsDto) { // <= ИСПРАВЛЕНО!
        return newsService.update(id, newsDto);                                 // <= ИСПРАВЛЕНО!
    }

    /**
     * Delete a news article by ID.
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        newsService.delete(id);
    }
}