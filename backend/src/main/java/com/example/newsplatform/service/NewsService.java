package com.example.newsplatform.service;

import com.example.newsplatform.dto.request.BulkActionRequestDto;
import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface NewsService {

    Page<NewsDto> searchAll(String search, String category, Pageable pageable);

    Page<NewsDto> searchPublished(String search, String category, Pageable pageable);

    NewsDto getPublishedById(Long id);

    Page<NewsDto> getPublishedByTermId(Long termId, Pageable pageable);

    Page<NewsDto> getPublishedByTermIds(List<Long> termIds, Pageable pageable);

    NewsDto create(NewsCreateRequestDto createRequest);

    NewsDto update(Long id, NewsUpdateRequestDto updateRequest);

    void delete(Long id);

    BulkActionRequestDto.BulkActionResult performBulkAction(BulkActionRequestDto request, Authentication authentication);

}