package com.example.newsplatform.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that HTML content contains only safe tags.
 * Allowed tags: img, b, i, a, u, strong, em
 */
@Documented
@Constraint(validatedBy = SafeHtmlValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeHtml {
    String message() default "HTML content contains unsafe tags";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}