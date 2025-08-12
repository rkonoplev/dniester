package com.example.newsplatform.controller;

import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.service.NewsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/news")
public class PublicNewsController {

    private final NewsService newsService;

    public PublicNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Get published news by ID
     */
    @GetMapping("/{id}")
    public NewsDto getPublishedNewsById(@PathVariable Long id) {
        return newsService.getPublishedById(id);
    }
}
