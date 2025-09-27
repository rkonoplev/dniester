package com.example.newsplatform.service;

import com.example.newsplatform.entity.User;
import com.example.newsplatform.repository.UserRepository;
import com.example.newsplatform.security.RoleConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling authorization logic and role-based access control.
 */
@Service
public class AuthorizationService {

    private final UserRepository userRepository;

    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Check if the authenticated user has ADMIN role
     */
    public boolean isAdmin(Authentication authentication) {
        return hasAuthority(authentication, RoleConstants.ROLE_ADMIN);
    }

    /**
     * Check if the authenticated user has EDITOR role
     */
    public boolean isEditor(Authentication authentication) {
        return hasAuthority(authentication, RoleConstants.ROLE_EDITOR);
    }

    /**
     * Check if the authenticated user has a specific authority
     */
    private boolean hasAuthority(Authentication authentication, String authority) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }

    /**
     * Get the current user's ID from authentication
     */
    public Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Optional<User> user = userRepository.findByUsername(authentication.getName());
        return user.map(User::getId).orElse(null);
    }

    /**
     * Check if the current user is the author of the specified news article
     */
    public boolean isAuthorOfNews(Authentication authentication, Long newsId) {
        Long currentUserId = getCurrentUserId(authentication);
        if (currentUserId == null || newsId == null) {
            return false;
        }

        // This method will be implemented in NewsService
        // For now, return false - we'll implement this in Phase 2
        return false;
    }

    /**
     * Check if user can access news (ADMIN or EDITOR who is author)
     */
    public boolean canAccessNews(Authentication authentication, Long newsId) {
        return isAdmin(authentication) ||
                (isEditor(authentication) && isAuthorOfNews(authentication, newsId));
    }
}