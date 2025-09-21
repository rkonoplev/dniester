package com.example.newsplatform.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enforce that a user must have at least one of the specified roles.
 * Roles are specified by their names.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAnyRole {
    /**
     * An array of required role names (e.g., ["ADMIN", "EDITOR"]).
     */
    String[] value();
}
