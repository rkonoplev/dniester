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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class NewsServiceImpl implements NewsService {

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

    @Override
    public Page<NewsDto> searchAll(String search, String category, Pageable pageable) {
        return newsRepository.searchAll(search, category, pageable)
                .map(NewsMapper::toDto);
    }

    public Page<NewsDto> searchAll(String search, String category, Pageable pageable, Authentication auth) {
        if (hasRoleId(auth, 1L)) {
            return newsRepository.searchAll(search, category, pageable).map(NewsMapper::toDto);
        } else if (hasRoleId(auth, 2L)) {
            Long authorId = getCurrentUserId(auth);
            return newsRepository.searchAllByAuthor(search, category, authorId, pageable).map(NewsMapper::toDto);
        }
        throw new org.springframework.security.access.AccessDeniedException("Insufficient permissions");
    }

    @Override
    public Page<NewsDto> searchPublished(String search, String category, Pageable pageable) {
        return newsRepository.searchPublished(search, category, pageable)
                .map(NewsMapper::toDto);
    }

    @Override
    public NewsDto getPublishedById(Long id) {
        News news = newsRepository.findByIdAndPublishedTrue(id)
                .orElseThrow(() -> new NotFoundException(NEWS_NOT_FOUND + id));
        return NewsMapper.toDto(news);
    }

    @Override
    public NewsDto create(NewsCreateRequestDto request) {
        News news = NewsMapper.fromCreateRequest(request);

        if (request.getAuthorId() != null) {
            User author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + request.getAuthorId()));
            news.setAuthor(author);
        }

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

    public boolean isAuthor(Long newsId, Long authorId) {
        return newsRepository.existsByIdAndAuthorId(newsId, authorId);
    }

    public Long getCurrentUserId(Authentication auth) {
        return null;
    }

    public boolean hasRoleId(Authentication auth, Long roleId) {
        return false;
    }
    
    public Set<Long> getCurrentUserRoleIds(Authentication auth) {
        return new HashSet<>();
    }

    @Override
    public NewsDto update(Long id, NewsUpdateRequestDto request) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NEWS_NOT_FOUND + id));

        NewsMapper.updateEntity(existing, request);

        if (request.getAuthorId() != null) {
            User author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + request.getAuthorId()));
            existing.setAuthor(author);
        }

        if (request.getCategoryId() != null) {
            Term term = termRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND + request.getCategoryId()));
            existing.setTerms(new HashSet<>(Set.of(term)));
        }

        News updated = newsRepository.save(existing);
        return NewsMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NEWS_NOT_FOUND + id));
        newsRepository.delete(existing);
    }

    @Override
    public Page<NewsDto> getPublishedByTermId(Long termId, Pageable pageable) {
        return newsRepository.findPublishedByTermId(termId, pageable)
                .map(NewsMapper::toDto);
    }

    @Override
    public Page<NewsDto> getPublishedByTermIds(java.util.List<Long> termIds, Pageable pageable) {
        if (termIds == null || termIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return newsRepository.findPublishedByTermIds(termIds, pageable)
                .map(NewsMapper::toDto);
    }
}