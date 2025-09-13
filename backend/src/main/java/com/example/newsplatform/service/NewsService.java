package com.example.newsplatform.service;

import com.example.newsplatform.dto.NewsCreateRequestDto;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    void performBulkAction(com.example.newsplatform.dto.BulkActionRequestDto request, 
                          org.springframework.security.core.Authentication auth);
}