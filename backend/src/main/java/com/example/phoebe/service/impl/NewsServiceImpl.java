package com.example.phoebe.service.impl;

import com.example.phoebe.dto.request.BulkActionRequestDto;
import com.example.phoebe.dto.request.NewsCreateRequestDto;
import com.example.phoebe.dto.request.NewsUpdateRequestDto;
import com.example.phoebe.dto.response.NewsDto;
import com.example.phoebe.entity.News;
import com.example.phoebe.entity.User;
import com.example.phoebe.exception.ResourceNotFoundException;
import com.example.phoebe.mapper.NewsMapper;
import com.example.phoebe.repository.NewsRepository;
import com.example.phoebe.repository.UserRepository;
import com.example.phoebe.service.NewsService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    private static final String ROLE_PREFIX = "ROLE_";
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final NewsMapper newsMapper;

    public NewsServiceImpl(NewsRepository newsRepository, UserRepository userRepository, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.newsMapper = newsMapper;
    }

    // --- Read Operations (Optimized with readOnly = true) ---

    /**
     * Finds all published news articles.
     * Annotated with @Transactional(readOnly = true) to give hints to the persistence provider (Hibernate)
     * that this transaction will not modify data. This allows for significant performance optimizations,
     * such as skipping dirty checks and unnecessary flushes.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NewsDto> findAllPublished(Pageable pageable) {
        return newsRepository.findByPublished(true, pageable).map(newsMapper::toDto);
    }

    /**
     * Finds a single published news article by its ID.
     * This method is optimized for reading and is cacheable.
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "news-by-id", key = "#id")
    public NewsDto findPublishedById(Long id) {
        News news = newsRepository.findByIdAndPublished(id, true)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
        return newsMapper.toDto(news);
    }

    /**
     * Finds all published news articles by a specific term ID. Optimized for reading.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NewsDto> findByTermId(Long termId, Pageable pageable) {
        return newsRepository.findByTermsIdAndPublished(termId, true, pageable)
                .map(newsMapper::toDto);
    }

    /**
     * Finds all published news articles by a list of term IDs. Optimized for reading.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NewsDto> findByTermIds(List<Long> termIds, Pageable pageable) {
        return newsRepository.findByTermsIdInAndPublished(termIds, true, pageable)
                .map(newsMapper::toDto);
    }

    /**
     * Finds all news articles for a specific user role (ADMIN or EDITOR). Optimized for reading.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<NewsDto> findAllForUser(Pageable pageable, Authentication authentication) {
        if (hasAdminRole(authentication)) {
            return newsRepository.findAll(pageable).map(newsMapper::toDto);
        } else if (hasEditorRole(authentication)) {
            User currentUser = getCurrentUser(authentication);
            return newsRepository.findByAuthorId(currentUser.getId(), pageable).map(newsMapper::toDto);
        }
        return Page.empty();
    }

    /**
     * Finds any news article by ID, intended for admin users. Optimized for reading.
     * Authorization is checked after fetching the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public NewsDto findById(Long id, Authentication authentication) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
        if (!canRead(news, authentication)) {
            throw new AccessDeniedException("You do not have permission to view this article.");
        }
        return newsMapper.toDto(news);
    }

    // --- Write Operations (Default read-write transaction) ---

    /**
     * Creates a new news article.
     * This is a write operation, so it uses a default @Transactional (read-write).
     */
    @Override
    @Transactional
    public NewsDto create(NewsCreateRequestDto request, Authentication authentication) {
        User author = getCurrentUser(authentication);
        News news = newsMapper.toEntity(request);
        news.setAuthor(author);
        News savedNews = newsRepository.save(news);
        return newsMapper.toDto(savedNews);
    }

    /**
     * Updates an existing news article.
     * This is a write operation, so it uses a default @Transactional.
     * Authorization is checked explicitly inside the method to avoid a double database fetch
     * that would occur if using @PreAuthorize.
     */
    @Override
    @Transactional
    @CacheEvict(value = "news-by-id", key = "#id")
    public NewsDto update(Long id, NewsUpdateRequestDto request, Authentication authentication) {
        // 1. Fetch the entity from the database once.
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));

        // 2. Perform authorization check on the fetched entity.
        if (!canUpdateOrDelete(news, authentication)) {
            throw new AccessDeniedException("Access Denied: You are not the author of this article or not an admin.");
        }

        // 3. Map DTO changes to the managed entity.
        newsMapper.updateEntityFromDto(request, news);

        // 4. No explicit .save() call is needed.
        // Because the method is @Transactional, Hibernate's dirty checking mechanism will detect
        // the changes to the 'news' entity and automatically issue an UPDATE statement upon commit.
        return newsMapper.toDto(news);
    }

    /**
     * Deletes a news article.
     * This is a write operation, using a default @Transactional.
     * Authorization is checked explicitly to avoid a double database fetch.
     */
    @Override
    @Transactional
    @CacheEvict(value = "news-by-id", key = "#id")
    public void delete(Long id, Authentication authentication) {
        // 1. Fetch the entity from the database once.
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));

        // 2. Perform authorization check on the fetched entity.
        if (!canUpdateOrDelete(news, authentication)) {
            throw new AccessDeniedException("Access Denied: You are not the author of this article or not an admin.");
        }

        // 3. Delete the entity.
        newsRepository.delete(news);
    }

    /**
     * Performs a bulk action (DELETE or UNPUBLISH) on a set of news articles.
     * This is a critical write operation restricted to ADMINs.
     */
    @Override
    @Transactional
    public BulkActionRequestDto.BulkActionResult performBulkAction(BulkActionRequestDto request, Authentication authentication) {
        if (!hasAdminRole(authentication)) {
            throw new AccessDeniedException("Bulk operations are restricted to ADMIN role only.");
        }
        if (!request.isConfirmed()) {
            throw new IllegalArgumentException("Bulk operation must be confirmed");
        }

        List<Long> targetIds = new ArrayList<>();
        switch (request.getFilterType()) {
            case BY_IDS:
                if (request.getItemIds() != null) {
                    targetIds.addAll(request.getItemIds());
                }
                break;
            case BY_TERM:
                targetIds.addAll(newsRepository.findIdsByTermId(request.getTermId()));
                break;
            case BY_AUTHOR:
                targetIds.addAll(newsRepository.findIdsByAuthorId(request.getAuthorId()));
                break;
            case ALL:
                targetIds.addAll(newsRepository.findAllIds());
                break;
        }

        if (targetIds.isEmpty()) {
            return new BulkActionRequestDto.BulkActionResult(0);
        }

        switch (request.getAction()) {
            case DELETE:
                newsRepository.deleteAllByIdInBatch(targetIds); // Use batch for performance
                break;
            case UNPUBLISH:
                newsRepository.unpublishByIds(targetIds);
                break;
            default:
                throw new IllegalArgumentException("Unsupported bulk action: " + request.getAction());
        }
        return new BulkActionRequestDto.BulkActionResult(targetIds.size());
    }

    // --- Authorization Helper Methods ---

    /**
     * Checks if a user can view a news article.
     * Currently allows all authenticated users to view any article through this admin-path,
     * but could be extended with more granular logic.
     */
    private boolean canRead(News news, Authentication authentication) {
        // For now, if you can access the admin API, you can read.
        // Public access is handled by findPublishedById.
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * Centralized logic to check if a user can modify or delete a news article.
     * @return true if the user is an ADMIN or the author of the news item.
     */
    private boolean canUpdateOrDelete(News news, Authentication authentication) {
        if (hasAdminRole(authentication)) {
            return true;
        }
        if (hasEditorRole(authentication)) {
            User currentUser = getCurrentUser(authentication);
            return news.getAuthor() != null && news.getAuthor().getId().equals(currentUser.getId());
        }
        return false;
    }

    // --- Role & User Helper Methods ---

    public boolean hasAdminRole(Authentication authentication) {
        return hasAuthority(authentication, "ADMIN");
    }

    public boolean hasEditorRole(Authentication authentication) {
        return hasAuthority(authentication, "EDITOR");
    }

    protected User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    private boolean hasAuthority(Authentication authentication, String roleName) {
        if (authentication == null) {
            return false;
        }
        String authorityName = ROLE_PREFIX + roleName;
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authorityName));
    }
}
