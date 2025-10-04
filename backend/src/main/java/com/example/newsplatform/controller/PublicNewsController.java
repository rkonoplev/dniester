package com.example.newsplatform.controller;

import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/news")
@Tag(name = "Public News API", description = "Endpoints for public access to news content")
public class PublicNewsController {

    private final NewsService newsService;

    public PublicNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    @Operation(summary = "Find all published news")
    public Page<NewsDto> findAllPublished(Pageable pageable) {
        return newsService.findAllPublished(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a published news article by ID")
    public NewsDto findPublishedById(@PathVariable Long id) {
        return newsService.findPublishedById(id);
    }
}