package com.example.newsplatform.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for SafeHtml annotation.
 * Checks that HTML content contains only allowed tags.
 */
public class SafeHtmlValidator implements ConstraintValidator<SafeHtml, String> {

    private static final Set<String> ALLOWED_TAGS = Set.of(
        "img", "b", "i", "a", "u", "strong", "em"
    );

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("</?([a-zA-Z][a-zA-Z0-9]*)[^>]*>");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        Matcher matcher = HTML_TAG_PATTERN.matcher(value);
        while (matcher.find()) {
            String tagName = matcher.group(1).toLowerCase();
            if (!ALLOWED_TAGS.contains(tagName)) {
                return false;
            }
        }
        return true;
    }
}