package com.example.newsplatform.controller;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/news")
public class AdminNewsController {

    private final NewsService newsService;

    public AdminNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    public List<NewsDto> getAllNews() {
        return newsService.getAllNews(); // all news, published or not
    }

    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public NewsDto getNewsById(@PathVariable Long id) {
        return newsService.getNewsById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_ADMIN")
    public NewsDto createNews(@Valid @RequestBody NewsCreateRequest request) {
        return newsService.createNews(request);
    }

    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public NewsDto updateNews(@PathVariable Long id, @Valid @RequestBody NewsUpdateRequest request) {
        return newsService.updateNews(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured("ROLE_ADMIN")
    public void deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
    }
}
