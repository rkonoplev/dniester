package com.example.newsplatform.service;

import com.example.newsplatform.dto.request.BulkActionRequestDto;
import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface NewsService {

    Page<NewsDto> searchAll(String filter, Pageable pageable);

    Page<NewsDto> searchPublished(String filter, Pageable pageable);

    NewsDto getPublishedById(Long id);

    Page<NewsDto> getByTermId(Long termId, Pageable pageable);

    Page<NewsDto> getByTermIds(Long[] termIds, Pageable pageable);

    NewsDto create(NewsCreateRequestDto createRequest);

    NewsDto update(Long id, NewsUpdateRequestDto updateRequest);

    void delete(Long id);

    void performBulkAction(BulkActionRequestDto request, Authentication auth);
}
