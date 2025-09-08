package com.example.newsplatform.service.impl;

import com.example.newsplatform.dto.NewsCreateRequestDto;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequestDto;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.Term;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.exception.NotFoundException;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.repository.TermRepository;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.service.NewsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link NewsService} using JpaRepository.
 * Handles business logic, entity mapping, and data integrity.
 *
 * Manages relationships:
 * - News ↔ User (author)
 * - News ↔ Term (categories)
 */
@Service
@Transactional
public class NewsServiceImpl implements NewsService {

    // Reusable error message constants to avoid string duplication
    private static final String NEWS_NOT_FOUND = "News not found with id ";
    private static final String USER_NOT_FOUND = "User not found with id: ";
    private static final String CATEGORY_NOT_FOUND = "Category not found with id: ";

    private final NewsRepository newsRepository;
    private final TermRepository termRepository;
    private final UserRepository userRepository;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository,
                           TermRepository termRepository,
                           UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.termRepository = termRepository;
        this.userRepository = userRepository;
    }

    /**
     * Searches all news (published and unpublished) with optional filters.
     */
    @Override
    public Page<NewsDto> searchAll(String search, String category, Pageable pageable) {
        return newsRepository.searchAll(search, category, pageable)
                .map(NewsMapper::toDto);
    }

    /**
     * Searches only published news with optional filters.
     */
    @Override
    public Page<NewsDto> searchPublished(String search, String category, Pageable pageable) {
        return newsRepository.searchPublished(search, category, pageable)
                .map(NewsMapper::toDto);
    }

    /**
     * Retrieves a published news item by its ID.
     *
     * @throws NotFoundException if no published news exists with the given ID
     */
    @Override
    public NewsDto getPublishedById(Long id) {
        News news = newsRepository.findByIdAndPublishedTrue(id)
                .orElseThrow(() -> new NotFoundException(NEWS_NOT_FOUND + id));
        return NewsMapper.toDto(news);
    }

    /**
     * Creates a new news item and sets its author and category if provided.
     *
     * @throws NotFoundException if the author or category does not exist
     */
    @Override
    public NewsDto create(NewsCreateRequestDto request) {
        News news = NewsMapper.fromCreateRequest(request);

        // Set author if provided
        if (request.getAuthorId() != null) {
            User author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + request.getAuthorId()));
            news.setAuthor(author);
        }

        // Set category if provided
        if (request.getCategoryId() != null) {
            Term term = termRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND + request.getCategoryId()));
            news.setTerms(new HashSet<>(Set.of(term)));
        } else {
            news.setTerms(new HashSet<>());
        }

        News saved = newsRepository.save(news);
        return NewsMapper.toDto(saved);
    }

    /**
     * Updates an existing news item with new values, including author and category.
     *
     * @throws NotFoundException if the news, author, or category does not exist
     */
    @Override
    public NewsDto update(Long id, NewsUpdateRequestDto request) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NEWS_NOT_FOUND + id));

        NewsMapper.updateEntity(existing, request);

        // Update author if provided
        if (request.getAuthorId() != null) {
            User author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + request.getAuthorId()));
            existing.setAuthor(author);
        }

        // Update category if provided
        if (request.getCategoryId() != null) {
            Term term = termRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND + request.getCategoryId()));
            existing.setTerms(new HashSet<>(Set.of(term)));
        }

        News updated = newsRepository.save(existing);
        return NewsMapper.toDto(updated);
    }

    /**
     * Deletes a news item by its ID.
     *
     * @throws NotFoundException if the news does not exist
     */
    @Override
    public void delete(Long id) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NEWS_NOT_FOUND + id));
        newsRepository.delete(existing);
    }

    /**
     * Gets published news by term ID with pagination.
     */
    @Override
    public Page<NewsDto> getPublishedByTermId(Long termId, Pageable pageable) {
        return newsRepository.findPublishedByTermId(termId, pageable)
                .map(NewsMapper::toDto);
    }

    /**
     * Gets published news by multiple term IDs with pagination.
     */
    @Override
    public Page<NewsDto> getPublishedByTermIds(java.util.List<Long> termIds, Pageable pageable) {
        if (termIds == null || termIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return newsRepository.findPublishedByTermIds(termIds, pageable)
                .map(NewsMapper::toDto);
    }
}
