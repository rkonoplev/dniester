package com.example.phoebe.security;

/**
 * Role constants for the application.
 * Defines role names and their corresponding authority strings.
 */
public class RoleConstants {

    // Role names (as stored in the database)
    public static final String ADMIN = "ADMIN";
    public static final String EDITOR = "EDITOR";

    // Spring Security authorities (with ROLE_ prefix)
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_EDITOR = "ROLE_EDITOR";

    // Role IDs (as defined in the database migration)
    public static final Long ADMIN_ID = 1L;
    public static final Long EDITOR_ID = 2L;

    private RoleConstants() {
        // Utility class - prevent instantiation
    }
}
