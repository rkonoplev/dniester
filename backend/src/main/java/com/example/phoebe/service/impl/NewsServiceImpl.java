package com.example.phoebe.service.impl;

import com.example.phoebe.dto.request.BulkActionRequestDto;
import com.example.phoebe.dto.request.NewsCreateRequestDto;
import com.example.phoebe.dto.request.NewsUpdateRequestDto;
import com.example.phoebe.dto.response.NewsDto;
import com.example.phoebe.entity.News;
import com.example.phoebe.entity.Term;
import com.example.phoebe.entity.User;
import com.example.phoebe.exception.ResourceNotFoundException;
import com.example.phoebe.mapper.NewsMapper;
import com.example.phoebe.repository.NewsRepository;
import com.example.phoebe.repository.TermRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private static final String ROLE_PREFIX = "ROLE_";
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final TermRepository termRepository;
    private final NewsMapper newsMapper;

    public NewsServiceImpl(NewsRepository newsRepository, UserRepository userRepository, TermRepository termRepository, NewsMapper newsMapper) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.termRepository = termRepository;
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
        return newsRepository.findByTermsIdAndPublished(termId, true, pageable).map(newsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsDto> findByTermIds(List<Long> termIds, Pageable pageable) {
        return newsRepository.findByTermsIdInAndPublished(termIds, true, pageable).map(newsMapper::toDto);
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

        if (request.getTermIds() != null && !request.getTermIds().isEmpty()) {
            Set<Term> terms = request.getTermIds().stream()
                    .map(termId -> termRepository.findById(termId)
                            .orElseThrow(() -> new ResourceNotFoundException("Term", "id", termId)))
                    .collect(Collectors.toSet());
            news.setTerms(terms);
        }

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
        // Optimization: No explicit .save() call is needed due to @Transactional and dirty checking.
        return newsMapper.toDto(existingNews);
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
                // Optimized for performance: uses a single DELETE statement for multiple entities.
                newsRepository.deleteAllByIdInBatch(targetIds);
                break;
            case UNPUBLISH:
                newsRepository.unpublishByIds(targetIds);
                break;
            default:
                throw new IllegalArgumentException("Unsupported bulk action: " + request.getAction());
        }
        return new BulkActionRequestDto.BulkActionResult(targetIds.size());
    }


    public boolean canAccessNews(Long newsId, Authentication authentication) {
        if (hasAdminRole(authentication)) {
            return true;
        }
        return isAuthor(newsId, authentication);
    }


    public boolean isAuthor(Long newsId, Authentication authentication) {
        if (newsId == null || authentication == null) {
            return false;
        }
        User currentUser = getCurrentUser(authentication);
        return newsRepository.existsByIdAndAuthorId(newsId, currentUser.getId());
    }


    public boolean hasAdminRole(Authentication authentication) {
        return hasAuthority(authentication, "ADMIN");
    }

    public boolean hasEditorRole(Authentication authentication) {
        return hasAuthority(authentication, "EDITOR");
    }

    private User getCurrentUser(Authentication authentication) {
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
