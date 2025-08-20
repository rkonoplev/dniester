package com.example.newsplatform.service;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing News entities.
 */
public interface NewsService {

    Page<NewsDto> searchAll(String search, String category, Pageable pageable);

    Page<NewsDto> searchPublished(String search, String category, Pageable pageable);

    NewsDto getPublishedById(Long id);

    NewsDto create(NewsCreateRequest request);

    NewsDto update(Long id, NewsUpdateRequest request);

    void delete(Long id);
}