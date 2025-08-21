package com.example.newsplatform.service.impl;

import com.example.newsplatform.dto.NewsCreateRequest;
import com.example.newsplatform.dto.NewsDto;
import com.example.newsplatform.dto.NewsUpdateRequest;
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
 * Implementation of NewsService using JpaRepository.
 * Handles business logic, entity mapping, and data integrity.
 *
 * Manages relationships:
 * - News ↔ User (author)
 * - News ↔ Term (categories)
 */
@Service
@Transactional
public class NewsServiceImpl implements NewsService {

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

        // Set author
        if (request.getAuthorId() != null) {
            User author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getAuthorId()));
            news.setAuthor(author);
        }

        // Set category
        if (request.getCategoryId() != null) {
            Term term = termRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId()));
            news.setTerms(new HashSet<>(Set.of(term)));
        } else {
            news.setTerms(new HashSet<>());
        }

        News saved = newsRepository.save(news);
        return NewsMapper.toDto(saved);
    }

    @Override
    public NewsDto update(Long id, NewsUpdateRequest request) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("News not found with id " + id));

        NewsMapper.updateEntity(existing, request);

        // Update author
        if (request.getAuthorId() != null) {
            User author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getAuthorId()));
            existing.setAuthor(author);
        }

        // Update category
        if (request.getCategoryId() != null) {
            Term term = termRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found with id: " + request.getCategoryId()));
            existing.setTerms(new HashSet<>(Set.of(term)));
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