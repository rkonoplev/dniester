package com.example.newsplatform.controller;

import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class PublicNewsController {

    private final NewsService newsService;

    public PublicNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    // Get list of published news
    @GetMapping
    public List<NewsDto> getAllNews() {
        return newsService.getAllPublishedNews();
    }

    // Get single news by id
    @GetMapping("/{id}")
    public NewsDto getNewsById(@PathVariable Long id) {
        return newsService.getPublishedNewsById(id);
    }
}
