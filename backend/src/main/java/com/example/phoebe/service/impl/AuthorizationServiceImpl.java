package com.example.phoebe.service.impl;

import com.example.phoebe.entity.News;
import com.example.phoebe.entity.User;
import com.example.phoebe.exception.ResourceNotFoundException;
import com.example.phoebe.repository.NewsRepository;
import com.example.phoebe.repository.UserRepository;
import com.example.phoebe.security.RoleConstants;
import com.example.phoebe.service.AuthorizationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AuthorizationService.
 * This service is the single source of truth for all authorization and role-based checks.
 */
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserRepository userRepository;
    private final NewsRepository newsRepository;

    public AuthorizationServiceImpl(UserRepository userRepository, NewsRepository newsRepository) {
        this.userRepository = userRepository;
        this.newsRepository = newsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAdmin(Authentication authentication) {
        return hasAuthority(authentication, RoleConstants.ROLE_ADMIN);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEditor(Authentication authentication) {
        return hasAuthority(authentication, RoleConstants.ROLE_EDITOR);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return userRepository.findIdByUsername(authentication.getName()).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAuthorOfNews(Authentication authentication, News news) {
        Long userId = getCurrentUserId(authentication);
        return news != null && userId != null && news.getAuthor().getId().equals(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAuthorOfNews(Authentication authentication, Long newsId) {
        if (newsId == null) {
            return false;
        }
        Long currentUserId = getCurrentUserId(authentication);
        if (currentUserId == null) {
            return false;
        }
        return newsRepository.existsByIdAndAuthorId(newsId, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUpdateOrDeleteNews(Authentication authentication, Long newsId) {
        if (isAdmin(authentication)) {
            return true;
        }
        return isAuthorOfNews(authentication, newsId);
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }
}
