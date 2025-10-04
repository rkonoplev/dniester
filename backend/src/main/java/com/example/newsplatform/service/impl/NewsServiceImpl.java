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

    @Override
    @Transactional(readOnly = true)
    public Page<NewsDto> findAllPublished(Pageable pageable) {
        return newsRepository.findByPublished(true, pageable).map(newsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "news-by-id", key = "#id")
    public NewsDto findPublishedById(Long id) {
        News news = newsRepository.findByIdAndPublished(id, true)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
        return newsMapper.toDto(news);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsDto> findByTermId(Long termId, Pageable pageable) {
        return newsRepository.findByTermsIdAndPublished(termId, true, pageable)
                .map(newsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsDto> findByTermIds(List<Long> termIds, Pageable pageable) {
        return newsRepository.findByTermsIdInAndPublished(termIds, true, pageable)
                .map(newsMapper::toDto);
    }

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

    @Override
    @Transactional(readOnly = true)
    public NewsDto findById(Long id, Authentication authentication) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
        if (!canAccessNews(id, authentication)) {
            throw new AccessDeniedException("You do not have permission to view this article.");
        }
        return newsMapper.toDto(news);
    }

    @Override
    @Transactional
    public NewsDto create(NewsCreateRequestDto request, Authentication authentication) {
        User author = getCurrentUser(authentication);
        News news = newsMapper.toEntity(request);
        news.setAuthor(author);
        News savedNews = newsRepository.save(news);
        return newsMapper.toDto(savedNews);
    }

    @Override
    @Transactional
    @CacheEvict(value = "news-by-id", key = "#id")
    public NewsDto update(Long id, NewsUpdateRequestDto request, Authentication authentication) {
        News existingNews = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
        verifyOwnershipOrAdmin(authentication, existingNews);
        newsMapper.updateEntityFromDto(request, existingNews);
        News updatedNews = newsRepository.save(existingNews);
        return newsMapper.toDto(updatedNews);
    }

    @Override
    @Transactional
    @CacheEvict(value = "news-by-id", key = "#id")
    public void delete(Long id, Authentication authentication) {
        News newsToDelete = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
        verifyOwnershipOrAdmin(authentication, newsToDelete);
        newsRepository.delete(newsToDelete);
    }

    @Override
    @Transactional
    public BulkActionRequestDto.BulkActionResult performBulkAction(BulkActionRequestDto request, Authentication authentication) {
        if (!hasRole(authentication, "ADMIN")) {
            throw new AccessDeniedException(
                    "Bulk operations are restricted to ADMIN role only. "
                            + "EDITOR can only delete single articles.");
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
                newsRepository.deleteAllById(targetIds);
                break;
            case UNPUBLISH:
                newsRepository.unpublishByIds(targetIds);
                break;
            default:
                throw new IllegalArgumentException("Unsupported bulk action: " + request.getAction());
        }
        return new BulkActionRequestDto.BulkActionResult(targetIds.size());
    }

    @Override
    public boolean canAccessNews(Long newsId, Authentication authentication) {
        if (hasAdminRole(authentication)) {
            return true;
        }
        return isAuthor(newsId, authentication);
    }

    @Override
    public boolean isAuthor(Long newsId, Authentication authentication) {
        if (newsId == null || authentication == null) {
            return false;
        }
        User currentUser = getCurrentUser(authentication);
        return newsRepository.existsByIdAndAuthorId(newsId, currentUser.getId());
    }

    @Override
    public boolean hasAdminRole(Authentication authentication) {
        return hasAuthority(authentication, "ADMIN");
    }

    @Override
    public boolean hasEditorRole(Authentication authentication) {
        return hasAuthority(authentication, "EDITOR");
    }

    /**
     * A generic role-checking helper method.
     * This is a convenience method for internal service logic.
     *
     * @param authentication The user's authentication object.
     * @param roleName       The name of the role to check (e.g., "ADMIN").
     * @return True if the user has the specified role.
     */
    public boolean hasRole(Authentication authentication, String roleName) {
        return hasAuthority(authentication, roleName);
    }

    /**
     * Retrieves the current authenticated user from the database.
     * Visibility is protected to allow spying in tests.
     * @param authentication The current user's authentication object.
     * @return The fetched {@link User} entity.
     */
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

    private void verifyOwnershipOrAdmin(Authentication authentication, News news) {
        if (hasAdminRole(authentication)) {
            return;
        }
        User currentUser = getCurrentUser(authentication);
        if (news.getAuthor() == null || !news.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access Denied: You are not the author of this article.");
        }
    }
}