package com.example.newsplatform.service;

import com.example.newsplatform.dto.request.BulkActionRequestDto;
import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Service interface for managing news articles.
 * Defines the contract for all news-related business operations, including
 * public-facing queries and administrative actions.
 */
public interface NewsService {

    // === Public, Read-Only Methods ===

    Page<NewsDto> findAllPublished(Pageable pageable);

    NewsDto findPublishedById(Long id);

    Page<NewsDto> findByTermId(Long termId, Pageable pageable);

    Page<NewsDto> findByTermIds(List<Long> termIds, Pageable pageable);


    // === Admin/Authenticated Methods ===

    Page<NewsDto> findAllForUser(Pageable pageable, Authentication authentication);

    NewsDto findById(Long id, Authentication authentication);

    NewsDto create(NewsCreateRequestDto request, Authentication authentication);

    NewsDto update(Long id, NewsUpdateRequestDto request, Authentication authentication);

    void delete(Long id, Authentication authentication);


    // === Bulk Operations ===

    BulkActionRequestDto.BulkActionResult performBulkAction(BulkActionRequestDto request, Authentication authentication);


    // === Authorization Helper Methods (for internal use in @PreAuthorize) ===

    boolean canAccessNews(Long newsId, Authentication authentication);

    boolean isAuthor(Long newsId, Authentication authentication);

    boolean hasAdminRole(Authentication authentication);

    boolean hasEditorRole(Authentication authentication);
}