package com.example.phoebe.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Service for testing role-based security aspects.
 */
@Service
public class RoleSecurityService {

    @PreAuthorize("hasRole('ADMIN')")
    public void adminOnlyMethod() {
        // Method that requires ADMIN role
    }
}