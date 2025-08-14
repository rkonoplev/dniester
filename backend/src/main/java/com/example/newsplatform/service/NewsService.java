package com.example.newsplatform.service;

import com.example.newsplatform.entity.News;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service layer for news business logic.
 * Handles search, CRUD, and mapping between entity and DTO.
 */
@Service
public class NewsService {

    private final NewsRepository newsRepository;

    @Autowired
    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    /**
     * Search all news (published or unpublished).
     * Case-insensitive search in both title and content.
     * Returns Page<NewsDto>
     */
    public Page<NewsDto> searchAll(String search, String category, Pageable pageable) {
        String keyword = (search != null) ? search.toLowerCase(Locale.ROOT) : null;
        return newsRepository.searchAll(keyword, category, pageable)
                .map(NewsMapper::toDto); // Converts Page<News> to Page<NewsDto>
    }

    /**
     * Search only published news. Returns Page<NewsDto>
     */
    public Page<NewsDto> searchPublished(String search, String category, Pageable pageable) {
        String keyword = (search != null) ? search.toLowerCase(Locale.ROOT) : null;
        return newsRepository.searchPublished(keyword, category, pageable)
                .map(NewsMapper::toDto);
    }

    /**
     * Create a new news article from DTO, returns DTO.
     */
    public NewsDto create(NewsDto newsDto) {
        News entity = NewsMapper.toEntity(newsDto);
        News saved = newsRepository.save(entity);
        return NewsMapper.toDto(saved);
    }

    /**
     * Update an existing news article.
     */
    public NewsDto update(Long id, NewsDto newsDto) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found with id: " + id));
        // Update only mutable fields
        existing.setTitle(newsDto.getTitle());
        existing.setContent(newsDto.getContent());
        // Add more fields if necessary (category, published, teaser, etc.)

        News updated = newsRepository.save(existing);
        return NewsMapper.toDto(updated);
    }

    /**
     * Delete a news article by ID.
     */
    public void delete(Long id) {
        newsRepository.deleteById(id);
    }

    /**
     * Get one published news article by ID.
     */
    public NewsDto getPublishedById(Long id) {
        News entity = newsRepository.findByIdAndPublishedTrue(id)
                .orElseThrow(() -> new RuntimeException("Published news not found with id: " + id));
        return NewsMapper.toDto(entity);
    }
}