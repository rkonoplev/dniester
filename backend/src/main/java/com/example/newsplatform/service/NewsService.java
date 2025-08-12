package com.example.newsplatform.service;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.model.News;
import com.example.newsplatform.repository.NewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsService {

    private final NewsRepository repository;
    private final NewsMapper mapper;

    public NewsService(NewsRepository repository, NewsMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Get all news with optional search and category filters
     */
    public Page<NewsDto> getAll(String search, String category, Pageable pageable) {
        Page<News> page;

        if ((search == null || search.isBlank()) && (category == null || category.isBlank())) {
            page = repository.findAll(pageable);
        } else if (search == null || search.isBlank()) {
            page = repository.findByCategory(category, pageable);
        } else if (category == null || category.isBlank()) {
            page = repository.searchByKeyword(search, pageable);
        } else {
            page = repository.searchByKeywordAndCategory(search, category, pageable);
        }

        return page.map(mapper::toDto);
    }

    @Transactional
    public NewsDto create(NewsCreateRequest request) {
        News news = mapper.fromCreateRequest(request);
        return mapper.toDto(repository.save(news));
    }

    /**
     * Get news by ID without publication check (for admin)
     */
    public NewsDto getById(Long id) {
        News news = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        return mapper.toDto(news);
    }

    /**
     * Get news by ID and check if published (for public)
     */
    public NewsDto getPublishedById(Long id) {
        News news = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));

        if (!news.isPublished()) {
            throw new RuntimeException("News not published");
        }

        return mapper.toDto(news);
    }

    @Transactional
    public NewsDto update(Long id, NewsUpdateRequest request) {
        News news = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        mapper.updateEntity(news, request);
        return mapper.toDto(repository.save(news));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
