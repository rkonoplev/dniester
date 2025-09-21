package com.example.newsplatform.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enforce that a user must have all of the specified roles.
 * Roles are specified by their names.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAllRoles {
    /**
     * An array of required role names (e.g., ["MODERATOR", "CONTENT_CREATOR"]).
     */
    String[] value();
}
