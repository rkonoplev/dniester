package com.example.newsplatform.controller;

import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Public controller for fetching published news.
 * Does not expose unpublished or draft articles.
 */
@RestController
@RequestMapping("/api/public/news")
public class PublicNewsController {

    private final NewsService newsService;

    @Autowired
    public PublicNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Search only published news by keyword and/or category.
     * Search is case-insensitive and looks in both title and content.
     */
    @GetMapping
    public Page<NewsDto> searchPublished(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        return newsService.searchPublished(search, category, pageable);
    }

    /**
     * Get one published news article by ID.
     */
    @GetMapping("/{id}")
    public NewsDto getPublishedById(@PathVariable Long id) {
        return newsService.getPublishedById(id);
    }
}