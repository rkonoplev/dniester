package com.example.newsplatform.security;

import org.springframework.stereotype.Service;

@Service
public class TestSecurityService {

    @RequireAnyRole({1L, 2L}) // ADMIN or EDITOR
    public void adminOrEditorMethod() {
        // Test method for security aspect
    }

    @RequireAllRoles({1L, 2L}) // Both ADMIN and EDITOR (unlikely scenario)
    public void adminAndEditorMethod() {
        // Test method for security aspect
    }

    @RequireRole({1L}) // Only ADMIN
    public void adminOnlyMethod() {
        // Test method for security aspect
    }
}