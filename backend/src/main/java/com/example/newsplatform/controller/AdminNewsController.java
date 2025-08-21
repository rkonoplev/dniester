package com.example.newsplatform.controller;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST controller for admin operations on news articles.
 * Endpoints require authentication and ADMIN role.
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
     *
     * @param search   Optional keyword to search in title or content (case-insensitive).
     * @param category Optional category filter.
     * @param pageable Pagination settings.
     * @return Paginated list of NewsDto.
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
     *
     * @param newsDto Validated input data.
     * @return Created NewsDto wrapped in 201 Created response.
     */
    @PostMapping
    public ResponseEntity<NewsDto> create(@RequestBody @Valid NewsDto newsDto) {
        NewsCreateRequest request = NewsMapper.newsDtoToCreateRequest(newsDto);
        NewsDto created = newsService.create(request);
        return ResponseEntity.ok(created);
    }

    /**
     * Update an existing news article.
     *
     * @param id      ID of the article to update.
     * @param newsDto Updated data.
     * @return Updated NewsDto.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NewsDto> update(@PathVariable Long id, @RequestBody @Valid NewsDto newsDto) {
        NewsUpdateRequest request = NewsMapper.newsDtoToUpdateRequest(newsDto);
        NewsDto updated = newsService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a news article by ID.
     *
     * @param id ID of the article to delete.
     * @return 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}