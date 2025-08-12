package com.example.newsplatform.controller;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Get paginated news list with optional search and category filters
     * @param search optional search keyword
     * @param category optional category filter
     * @param pageable pagination info
     * @return page of NewsDto
     */
    @GetMapping
    public Page<NewsDto> getAllNews(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        return newsService.getAll(search, category, pageable);
    }

    @PostMapping
    public NewsDto createNews(@RequestBody NewsCreateRequest request) {
        return newsService.create(request);
    }

    /**
     * Get news by ID; published check done in service
     * @param id news ID
     * @return NewsDto
     */
    @GetMapping("/{id}")
    public NewsDto getNewsById(@PathVariable Long id) {
        return newsService.getById(id);
    }

    @PutMapping("/{id}")
    public NewsDto updateNews(@PathVariable Long id, @RequestBody NewsUpdateRequest request) {
        return newsService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable Long id) {
        newsService.delete(id);
    }
}
