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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public Page<NewsDto> searchAll(String search, String category, Pageable pageable) {
        return newsRepository.searchAll(search, category, pageable).map(newsMapper::toDto);
    }

    @Override
    public Page<NewsDto> searchPublished(String search, String category, Pageable pageable) {
        return newsRepository.searchPublished(search, category, pageable).map(newsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public NewsDto getPublishedById(Long id) {
        News news = newsRepository.findByIdAndPublished(id, true)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));
        return newsMapper.toDto(news);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsDto> getPublishedByTermId(Long termId, Pageable pageable) {
        return newsRepository.findByTerms_IdAndPublished(termId, true, pageable).map(newsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NewsDto> getPublishedByTermIds(List<Long> termIds, Pageable pageable) {
        return newsRepository.findByTerms_IdInAndPublished(termIds, true, pageable).map(newsMapper::toDto);
    }

    @Override
    @Transactional
    public NewsDto create(NewsCreateRequestDto createRequest) {
        Authentication authentication = getAuthenticatedUser();
        String currentUsername = authentication.getName();
        User author = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

        News news = newsMapper.toEntity(createRequest);
        news.setAuthor(author);

        News savedNews = newsRepository.save(news);
        return newsMapper.toDto(savedNews);
    }

    @Override
    @Transactional
    public NewsDto update(Long id, NewsUpdateRequestDto updateRequest) {
        News existingNews = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));

        Authentication authentication = getAuthenticatedUser();
        verifyOwnershipOrAdmin(authentication, existingNews);

        // Use the mapper to update the entity from the DTO
        newsMapper.updateEntityFromDto(updateRequest, existingNews);

        News updatedNews = newsRepository.save(existingNews);
        return newsMapper.toDto(updatedNews);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        News newsToDelete = newsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("News", "id", id));

        Authentication authentication = getAuthenticatedUser();
        verifyOwnershipOrAdmin(authentication, newsToDelete);

        newsRepository.delete(newsToDelete);
    }

    @Override
    @Transactional
    public BulkActionRequestDto.BulkActionResult performBulkAction(BulkActionRequestDto request, Authentication authentication) {
        if (!hasRole(authentication, "ADMIN")) {
            throw new AccessDeniedException("Bulk operations are restricted to ADMIN role only. EDITOR can only delete single articles.");
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

        if (request.getAction() == null) {
            throw new IllegalArgumentException("Unsupported bulk action");
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

    public boolean hasRole(Authentication authentication, String roleName) {
        if (authentication == null || roleName == null) {
            return false;
        }
        String authorityName = ROLE_PREFIX + roleName.toUpperCase();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authorityName));
    }

    private void verifyOwnershipOrAdmin(Authentication authentication, News news) {
        if (hasRole(authentication, "ADMIN")) {
            return; // Admins can do anything
        }

        String currentUsername = authentication.getName();
        User author = news.getAuthor();

        if (author == null || !author.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Access Denied: You are not the author of this article.");
        }

        // For added security, especially if usernames can change, check by ID if the principal is a User object
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            // In a real app, you'd resolve your custom UserDetails object here
            // For now, the username check is sufficient.
        }
    }

    private Authentication getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException("Access Denied: User is not authenticated.");
        }
        return authentication;
    }
}