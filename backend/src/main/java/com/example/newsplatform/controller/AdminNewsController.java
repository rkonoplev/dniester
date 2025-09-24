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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for admin operations on news articles.
 * Endpoints require authentication and ADMIN/EDITOR role.
 */
@RestController
@RequestMapping("/api/admin/news")
@Tag(name = "Admin News API", description = "Endpoints for administrators/editors to manage news content")
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
    @Operation(summary = "Search all news",
            description = "Search all news items (both published and unpublished) " +
                    "with optional keyword and category filters.")
    @ApiResponse(responseCode = "200", description = "Search executed successfully")
    public Page<NewsDto> searchAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            Pageable pageable) {
        return newsService.searchAll(search, category, pageable);
    }

    /**
     * Create a new news article.
     *
     * @param createRequest Validated input data.
     * @return Created NewsDto wrapped in 201 Created response.
     */
    @PostMapping
    @Operation(summary = "Create a new article",
            description = "Creates a new news article. Requires ADMIN/EDITOR role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "News article successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<NewsDto> create(@RequestBody @Valid NewsCreateRequestDto createRequest) {
        NewsDto created = newsService.create(createRequest);
        // Return 201 Created instead of 200 OK
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing news article.
     *
     * @param id      ID of the article to update.
     * @param updateRequest Updated data.
     * @return Updated NewsDto.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an article",
            description = "Updates an existing article by ID. Requires ADMIN/EDITOR role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "News article successfully updated"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<NewsDto> update(@PathVariable Long id,
                                          @RequestBody @Valid NewsUpdateRequestDto updateRequest) {
        NewsDto updated = newsService.update(id, updateRequest);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a news article by ID.
     *
     * @param id ID of the article to delete.
     * @return 204 No Content if deleted, 404 if not found.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an article",
            description = "Deletes a news article by ID. Requires ADMIN/EDITOR role.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // our NewsService throws NotFoundException if article doesn't exist (handled by GlobalExceptionHandler)
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Perform bulk operations on news articles.
     * RESTRICTION: Only ADMIN role can perform bulk operations.
     * EDITOR role is limited to single article operations only.
     *
     * @param request bulk action request (delete/unpublish with filters)
     * @param authentication current user authentication
     * @return A DTO with the count of affected items.
     */
    @PostMapping("/bulk")
    @Operation(summary = "Bulk operations on articles",
            description = "Perform bulk delete or unpublish operations. " +
                    "RESTRICTED: Only ADMIN role allowed. EDITOR cannot perform bulk operations.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bulk operation completed successfully"),
            @ApiResponse(responseCode = "403",
                    description = "Access denied - EDITOR role cannot perform bulk operations"),
            @ApiResponse(responseCode = "400", description = "Invalid request or operation not confirmed")
    })
    public ResponseEntity<BulkActionRequestDto.BulkActionResult> performBulkAction(
            @RequestBody @Valid BulkActionRequestDto request,
            Authentication authentication) {
        BulkActionRequestDto.BulkActionResult result = newsService.performBulkAction(request, authentication);
        return ResponseEntity.ok(result);
    }
}