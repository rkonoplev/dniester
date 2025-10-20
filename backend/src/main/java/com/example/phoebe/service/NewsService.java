package com.example.phoebe.service;

import com.example.phoebe.dto.request.BulkActionRequestDto;
import com.example.phoebe.dto.request.NewsCreateRequestDto;
import com.example.phoebe.dto.request.NewsUpdateRequestDto;
import com.example.phoebe.dto.response.NewsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Service interface for managing news articles.
 * Defines the contract for all news-related business operations.
 */
public interface NewsService {

    Page<NewsDto> findAllPublished(Pageable pageable);

    NewsDto findPublishedById(Long id);

    Page<NewsDto> findByTermId(Long termId, Pageable pageable);

    Page<NewsDto> findByTermIds(List<Long> termIds, Pageable pageable);

    Page<NewsDto> findAllForUser(Pageable pageable, Authentication authentication);

    NewsDto findById(Long id, Authentication authentication);

    NewsDto create(NewsCreateRequestDto request, Authentication authentication);

    NewsDto update(Long id, NewsUpdateRequestDto request, Authentication authentication);

    void delete(Long id, Authentication authentication);

    BulkActionRequestDto.BulkActionResult performBulkAction(BulkActionRequestDto request,
                                                            Authentication authentication);
}
