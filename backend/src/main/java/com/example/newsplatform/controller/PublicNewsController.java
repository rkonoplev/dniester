package com.example.newsplatform.controller;

import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

// ✅ Swagger annotations
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Public controller for fetching published news.
 * Does not expose unpublished or draft articles.
 */
@RestController
@RequestMapping("/api/public/news")
@Tag(name = "Public News API", description = "Endpoints for public consumers to fetch published news articles")
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
    @Operation(summary = "Search published news", description = "Search only published news by optional keyword or category.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search executed successfully")
    })
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
    @Operation(summary = "Get published article by ID", description = "Retrieve one published article by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article found"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public NewsDto getPublishedById(@PathVariable Long id) {
        return newsService.getPublishedById(id);
    }
}