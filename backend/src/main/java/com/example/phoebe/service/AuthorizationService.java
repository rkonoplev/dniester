package com.example.phoebe.service;

import com.example.phoebe.entity.News;
import com.example.phoebe.entity.User;
import org.springframework.security.core.Authentication;

/**
 * Service interface for handling all authorization and role-based checks.
 * This is the single source of truth for permission checks in the application.
 */
public interface AuthorizationService {

    boolean isAdmin(Authentication authentication);

    boolean isEditor(Authentication authentication);

    Long getCurrentUserId(Authentication authentication);

    User getCurrentUser(Authentication authentication);

    boolean isAuthorOfNews(Authentication authentication, News news);

    boolean isAuthorOfNews(Authentication authentication, Long newsId);

    boolean canUpdateOrDeleteNews(Authentication authentication, Long newsId);
}
