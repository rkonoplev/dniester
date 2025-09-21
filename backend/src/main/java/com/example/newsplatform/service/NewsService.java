package com.example.newsplatform.service;

import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

/**
 * Service interface for managing News entities.
 */
public interface NewsService {

    Page<NewsDto> searchAll(String search, String category, Pageable pageable);

    Page<NewsDto> searchPublished(String search, String category, Pageable pageable);

    NewsDto getPublishedById(Long id);

    NewsDto create(NewsCreateRequestDto request);

    NewsDto update(Long id, NewsUpdateRequestDto request);

    void delete(Long id);

    /**
     * Get published news by term ID with pagination.
     */
    Page<NewsDto> getPublishedByTermId(Long termId, Pageable pageable);

    /**
     * Get published news by multiple term IDs with pagination.
     */
    Page<NewsDto> getPublishedByTermIds(java.util.List<Long> termIds, Pageable pageable);

    /**
     * Perform bulk operations on news articles.
     * Only ADMIN role can perform bulk delete operations.
     * EDITOR role is restricted to single article operations only.
     */
    void performBulkAction(com.example.newsplatform.dto.request.BulkActionRequestDto request,
                           Authentication auth);

    /**
     * Checks if a user (by ID) is the author of a specific news article.
     *
     * @param newsId   The ID of the news article.
     * @param authorId The ID of the user.
     * @return true if the user is the author, false otherwise.
     */
    boolean isAuthor(Long newsId, Long authorId);

    /**
     * Checks if the authenticated user has a specific role by its ID.
     * This is used for fine-grained access control, e.g., only ADMIN (role ID = 1) can perform bulk actions.
     *
     * @param auth     The current user's authentication context.
     * @param roleId   The ID of the role to check for.
     * @return true if the user has the specified role, false otherwise.
     */
    boolean hasRoleId(Authentication auth, Long roleId);
}