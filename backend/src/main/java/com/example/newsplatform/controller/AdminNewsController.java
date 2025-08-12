package com.example.newsplatform.controller;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/news")
public class AdminNewsController {

    private final NewsService newsService;

    public AdminNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public Page<NewsDto> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            Pageable pageable
    ) {
        return newsService.getAll(search, category, pageable);
    }

    @GetMapping("/{id}")
    public NewsDto getById(@PathVariable Long id) {
        return newsService.getById(id);
    }

    @PostMapping
    public NewsDto create(@RequestBody NewsCreateRequest request) {
        return newsService.create(request);
    }

    @PutMapping("/{id}")
    public NewsDto update(@PathVariable Long id, @RequestBody NewsUpdateRequest request) {
        return newsService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        newsService.delete(id);
    }
}
