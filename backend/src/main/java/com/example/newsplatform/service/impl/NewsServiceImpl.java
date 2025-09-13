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

/**
 * Service implementation for news management operations.
 * Handles CRUD operations, search functionality, and role-based access control.
 */
@Service
@Transactional
public class NewsServiceImpl implements NewsService {

    // Error message constants
    private static final String NEWS_NOT_FOUND = "News not found with id ";
    private static final String USER_NOT_FOUND = "User not found with id: ";
    private static final String CATEGORY_NOT_FOUND = "Category not found with id: ";

    private final NewsRepository newsRepository;
    private final TermRepository termRepository;
    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection.
     *
     * @param newsRepository repository for news operations
     * @param termRepository repository for term/category operations
     * @param userRepository repository for user operations
     */
    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository,
                           TermRepository termRepository,
                           UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.termRepository = termRepository;
        this.userRepository = userRepository;
    }

    /**
     * Search all news articles (published and unpublished) - admin access.
     *
     * @param search search term for title/content filtering
     * @param category category filter
     * @param pageable pagination parameters
     * @return paginated list of news DTOs
     */
    @Override
    public Page<NewsDto> searchAll(String search, String category, Pageable pageable) {
        return newsRepository.searchAll(search, category, pageable)
                .map(NewsMapper::toDto);
    }

    /**
     * Search news with role-based filtering.
     * ADMIN (role 1): sees all news
     * EDITOR (role 2): sees only their own news
     *
     * @param search search term for filtering
     * @param category category filter
     * @param pageable pagination parameters
     * @param auth authentication context
     * @return paginated list of news DTOs based on user role
     * @throws AccessDeniedException if user lacks sufficient permissions
     */
    public Page<NewsDto> searchAll(String search, String category, Pageable pageable, Authentication auth) {
        if (hasRoleId(auth, 1L)) {
            // Admin can see all news
            return newsRepository.searchAll(search, category, pageable).map(NewsMapper::toDto);
        } else if (hasRoleId(auth, 2L)) {
            // Editor can only see their own news
            Long authorId = getCurrentUserId(auth);
            return newsRepository.searchAllByAuthor(search, category, authorId, pageable).map(NewsMapper::toDto);
        }
        throw new org.springframework.security.access.AccessDeniedException("Insufficient permissions");
    }

    /**
     * Search only published news articles - public access.
     *
     * @param search search term for title/content filtering
     * @param category category filter
     * @param pageable pagination parameters
     * @return paginated list of published news DTOs
     */
    @Override
    public Page<NewsDto> searchPublished(String search, String category, Pageable pageable) {
        return newsRepository.searchPublished(search, category, pageable)
                .map(NewsMapper::toDto);
    }

    /**
     * Get a single published news article by ID - public access.
     *
     * @param id news article ID
     * @return news DTO if found and published
     * @throws NotFoundException if news not found or not published
     */
    @Override
    public NewsDto getPublishedById(Long id) {
        News news = newsRepository.findByIdAndPublishedTrue(id)
                .orElseThrow(() -> new NotFoundException(NEWS_NOT_FOUND + id));
        return NewsMapper.toDto(news);
    }

    /**
     * Create a new news article.
     *
     * @param request news creation request with title, content, author, and category
     * @return created news DTO
     * @throws NotFoundException if author or category not found
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

        // Set category/term if provided
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
     * Check if user is the author of a specific news article.
     *
     * @param newsId news article ID
     * @param authorId user ID to check
     * @return true if user is the author
     */
    public boolean isAuthor(Long newsId, Long authorId) {
        return newsRepository.existsByIdAndAuthorId(newsId, authorId);
    }

    /**
     * Get current user ID from authentication context.
     * TODO: Implement when OAuth 2.0 + 2FA is integrated
     *
     * @param auth authentication context
     * @return current user ID
     */
    public Long getCurrentUserId(Authentication auth) {
        return null;
    }

    /**
     * Check if current user has specific role ID.
     * TODO: Implement when OAuth 2.0 + 2FA is integrated
     *
     * @param auth authentication context
     * @param roleId role ID to check
     * @return true if user has the role
     */
    public boolean hasRoleId(Authentication auth, Long roleId) {
        return false;
    }
    
    /**
     * Get all role IDs for current user.
     * TODO: Implement when OAuth 2.0 + 2FA is integrated
     *
     * @param auth authentication context
     * @return set of role IDs
     */
    public Set<Long> getCurrentUserRoleIds(Authentication auth) {
        return new HashSet<>();
    }

    /**
     * Update an existing news article.
     *
     * @param id news article ID to update
     * @param request update request with new values
     * @return updated news DTO
     * @throws NotFoundException if news, author, or category not found
     */
    @Override
    public NewsDto update(Long id, NewsUpdateRequestDto request) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NEWS_NOT_FOUND + id));

        // Update basic fields
        NewsMapper.updateEntity(existing, request);

        // Update author if provided
        if (request.getAuthorId() != null) {
            User author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND + request.getAuthorId()));
            existing.setAuthor(author);
        }

        // Update category/term if provided
        if (request.getCategoryId() != null) {
            Term term = termRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new NotFoundException(CATEGORY_NOT_FOUND + request.getCategoryId()));
            existing.setTerms(new HashSet<>(Set.of(term)));
        }

        News updated = newsRepository.save(existing);
        return NewsMapper.toDto(updated);
    }

    /**
     * Delete a news article by ID.
     *
     * @param id news article ID to delete
     * @throws NotFoundException if news not found
     */
    @Override
    public void delete(Long id) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NEWS_NOT_FOUND + id));
        newsRepository.delete(existing);
    }

    /**
     * Get published news articles filtered by a specific term/category ID.
     *
     * @param termId term/category ID to filter by
     * @param pageable pagination parameters
     * @return paginated list of published news DTOs
     */
    @Override
    public Page<NewsDto> getPublishedByTermId(Long termId, Pageable pageable) {
        return newsRepository.findPublishedByTermId(termId, pageable)
                .map(NewsMapper::toDto);
    }

    /**
     * Get published news articles filtered by multiple term/category IDs.
     * Articles matching any of the provided term IDs will be returned.
     *
     * @param termIds list of term/category IDs to filter by
     * @param pageable pagination parameters
     * @return paginated list of published news DTOs, empty page if no term IDs provided
     */
    @Override
    public Page<NewsDto> getPublishedByTermIds(java.util.List<Long> termIds, Pageable pageable) {
        if (termIds == null || termIds.isEmpty()) {
            return Page.empty(pageable);
        }
        return newsRepository.findPublishedByTermIds(termIds, pageable)
                .map(NewsMapper::toDto);
    }

    /**
     * Perform bulk operations on news articles with role-based restrictions.
     * ADMIN: Can perform any bulk operation (delete, unpublish)
     * EDITOR: Restricted to single article operations only - bulk operations denied
     *
     * @param request bulk action request with operation type and filters
     * @param auth authentication context
     * @throws AccessDeniedException if EDITOR attempts bulk operations
     * @throws IllegalArgumentException if request is invalid
     */
    @Override
    public void performBulkAction(com.example.newsplatform.dto.BulkActionRequestDto request, 
                                 org.springframework.security.core.Authentication auth) {
        // Only ADMIN can perform bulk operations
        if (!hasRoleId(auth, 1L)) {
            throw new org.springframework.security.access.AccessDeniedException(
                "Bulk operations are restricted to ADMIN role only. EDITOR can only delete single articles.");
        }

        if (!request.isConfirmed()) {
            throw new IllegalArgumentException("Bulk operation must be confirmed");
        }

        java.util.List<Long> targetIds = getTargetIds(request);
        
        if (targetIds.isEmpty()) {
            return; // No items to process
        }

        switch (request.getAction()) {
            case DELETE:
                // Bulk delete - ADMIN only
                newsRepository.deleteAllById(targetIds);
                break;
            case UNPUBLISH:
                // Bulk unpublish - ADMIN only
                newsRepository.unpublishByIds(targetIds);
                break;
            default:
                throw new IllegalArgumentException("Unsupported bulk action: " + request.getAction());
        }
    }

    /**
     * Get target article IDs based on filter criteria.
     *
     * @param request bulk action request with filter parameters
     * @return list of article IDs to process
     */
    private java.util.List<Long> getTargetIds(com.example.newsplatform.dto.BulkActionRequestDto request) {
        switch (request.getFilterType()) {
            case BY_IDS:
                return request.getItemIds() != null ? 
                    new java.util.ArrayList<>(request.getItemIds()) : 
                    java.util.Collections.emptyList();
            case BY_TERM:
                if (request.getTermId() != null) {
                    return newsRepository.findIdsByTermId(request.getTermId());
                }
                break;
            case BY_AUTHOR:
                if (request.getAuthorId() != null) {
                    return newsRepository.findIdsByAuthorId(request.getAuthorId());
                }
                break;
            case ALL:
                return newsRepository.findAllIds();
            default:
                throw new IllegalArgumentException("Unsupported filter type: " + request.getFilterType());
        }
        return java.util.Collections.emptyList();
    }
}