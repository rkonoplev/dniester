package com.example.newsplatform.service.impl;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.Term;
import com.example.newsplatform.exception.NotFoundException;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.service.NewsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

/**
 * Implementation of NewsService using JpaRepository.
 */
@Service
@Transactional
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Override
    public Page<NewsDto> searchAll(String search, String category, Pageable pageable) {
        return newsRepository.searchAll(search, category, pageable)
                .map(NewsMapper::toDto);
    }

    @Override
    public Page<NewsDto> searchPublished(String search, String category, Pageable pageable) {
        return newsRepository.searchPublished(search, category, pageable)
                .map(NewsMapper::toDto);
    }

    @Override
    public NewsDto getPublishedById(Long id) {
        News news = newsRepository.findByIdAndPublishedTrue(id)
                .orElseThrow(() -> new NotFoundException("News not found with id " + id));
        return NewsMapper.toDto(news);
    }

    @Override
    public NewsDto create(NewsCreateRequest request) {
        News news = NewsMapper.fromCreateRequest(request);

        // Category handling (simple case: single category string → Term placeholder)
        if (request.getCategory() != null) {
            Term term = new Term();
            term.setName(request.getCategory());
            news.setTerms(new HashSet<>(Collections.singleton(term)));
        }

        News saved = newsRepository.save(news);
        return NewsMapper.toDto(saved);
    }

    @Override
    public NewsDto update(Long id, NewsUpdateRequest request) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id " + id));

        NewsMapper.updateEntity(existing, request);

        if (request.getCategory() != null) {
            Term term = new Term();
            term.setName(request.getCategory());
            existing.setTerms(new HashSet<>(Collections.singleton(term)));
        }

        News updated = newsRepository.save(existing);
        return NewsMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id " + id));
        newsRepository.delete(existing);
    }
}