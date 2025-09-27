package com.example.newsplatform.controller;

import com.example.newsplatform.dto.request.BulkActionRequestDto;
import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/news")
@Tag(name = "Admin News API", description = "Endpoints for administrators/editors to manage news content")
public class AdminNewsController {

    private final NewsService newsService;

    public AdminNewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    @Operation(summary = "Find all news", description = "Finds all news articles, respecting user roles (ADMIN sees all, EDITOR sees own).")
    public Page<NewsDto> findAll(Pageable pageable, Authentication authentication) {
        return newsService.findAllForUser(pageable, authentication);
    }

    @PostMapping
    @Operation(summary = "Create a new article")
    public ResponseEntity<NewsDto> create(@RequestBody @Valid NewsCreateRequestDto createRequest, Authentication authentication) {
        NewsDto created = newsService.create(createRequest, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an article")
    public ResponseEntity<NewsDto> update(@PathVariable Long id, @RequestBody @Valid NewsUpdateRequestDto updateRequest, Authentication authentication) {
        NewsDto updated = newsService.update(id, updateRequest, authentication);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an article")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
        newsService.delete(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    @Operation(summary = "Bulk operations on articles")
    public ResponseEntity<BulkActionRequestDto.BulkActionResult> performBulkAction(@RequestBody @Valid BulkActionRequestDto request, Authentication authentication) {
        BulkActionRequestDto.BulkActionResult result = newsService.performBulkAction(request, authentication);
        return ResponseEntity.ok(result);
    }
}