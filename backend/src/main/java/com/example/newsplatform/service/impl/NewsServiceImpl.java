package com.example.newsplatform.service.impl;

import com.example.newsplatform.dto.request.BulkActionRequestDto;
import com.example.newsplatform.dto.request.NewsCreateRequestDto;
import com.example.newsplatform.dto.request.NewsUpdateRequestDto;
import com.example.newsplatform.dto.response.NewsDto;
import com.example.newsplatform.entity.News;
import com.example.newsplatform.entity.User;
import com.example.newsplatform.exception.ResourceNotFoundException;
import com.example.newsplatform.mapper.NewsMapper;
import com.example.newsplatform.repository.NewsRepository;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing News entities.
 * Handles business logic for creating, reading, updating, deleting, and searching news articles.
 * Also implements bulk operations and authorization checks for administrative users.
 */
@Service
@Transactional
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final NewsMapper newsMapper;

    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository, UserRepository userRepository, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.newsMapper = newsMapper;
    }

    /**
     * Retrieves a paginated list of all news articles (both published and unpublished),
     * optionally filtered by search term and category.
     */
    @Override
    public Page<NewsDto> searchAll(String search, String category, Pageable pageable) {
        return newsRepository.searchAll(search, category, pageable)
                .map(news -> newsMapper.toDto(news));
    }

    /**
     * Retrieves a paginated list of only published news articles,
     * optionally filtered by search term and category.
     */
    @Override
    public Page<NewsDto> searchPublished(String search, String category, Pageable pageable) {
        return newsRepository.searchPublished(search, category, pageable)
                .map(news -> newsMapper.toDto(news));
    }

    /**
     * Retrieves a single published news article by its ID.
     */
    @Override
    public NewsDto getPublishedById(Long id) {
        News news = newsRepository.findByIdAndPublishedTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
        return newsMapper.toDto(news);
    }

    /**
     * Creates a new news article. The author is automatically set to the currently authenticated user.
     */
    @Override
    public NewsDto create(NewsCreateRequestDto createRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        News news = newsMapper.fromCreateRequest(createRequest);
        news.setAuthor(author);

        return newsMapper.toDto(newsRepository.save(news));
    }

    /**
     * Updates an existing news article by its ID.
     */
    @Override
    public NewsDto update(Long id, NewsUpdateRequestDto updateRequest) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));

        newsMapper.updateEntity(updateRequest, news);
        return newsMapper.toDto(newsRepository.save(news));
    }

    /**
     * Deletes a news article by its ID.
     */
    @Override
    public void delete(Long id) {
        if (!newsRepository.existsById(id)) {
            throw new ResourceNotFoundException("News", "id", id);
        }
        newsRepository.deleteById(id);
    }

    /**
     * Retrieves a paginated list of published news articles associated with a specific term ID.
     */
    @Override
    public Page<NewsDto> getPublishedByTermId(Long termId, Pageable pageable) {
        return newsRepository.findPublishedByTermId(termId, pageable)
                .map(news -> newsMapper.toDto(news));
    }

    /**
     * Retrieves a paginated list of published news articles associated with any of the provided term IDs.
     */
    @Override
    public Page<NewsDto> getPublishedByTermIds(List<Long> termIds, Pageable pageable) {
        return newsRepository.findPublishedByTermIds(termIds, pageable)
                .map(news -> newsMapper.toDto(news));
    }

    /**
     * Performs bulk operations (e.g., delete, unpublish) on news articles.
     * Access control and specific logic should be implemented here based on the user's role and the request.
     */
    @Override
    public void performBulkAction(BulkActionRequestDto request, Authentication auth) {
        // TODO: Implement the actual bulk operation logic here.
        // This should include:
        // 1. Validating the user's role (e.g., ADMIN) from the 'auth' object.
        // 2. Determining which articles to target (e.g., by IDs, term, author).
        // 3. Executing the requested action (e.g., delete, unpublish) via NewsRepository.
        // For now, this is a stub.
        throw new UnsupportedOperationException("Bulk action is not yet implemented.");
    }

    /**
     * Checks if the specified user is the author of the news article.
     * Delegates to the repository for efficient database-level check.
     */
    @Override
    public boolean isAuthor(Long newsId, Long authorId) {
        return newsRepository.existsByIdAndAuthorId(newsId, authorId);
    }

    /**
     * Determines if the currently authenticated user has a specific role.
     * Extracts the user's roles from the Authentication object and checks for a match.
     */
    @Override
    public boolean hasRoleId(Authentication auth, Long roleId) {
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }

        // Предполагается, что GrantedAuthority возвращает строки вида "ROLE_1", "ROLE_2" и т.д.
        String targetRole = "ROLE_" + roleId;

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(targetRole::equals);
    }
}