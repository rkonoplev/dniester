package com.example.phoebe.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URI;

/**
 * Validator for URL format.
 */
public class UrlValidator implements ConstraintValidator<ValidUrl, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Allow null/empty values
        }
        
        try {
            URI.create(value).toURL();
            return value.startsWith("http://") || value.startsWith("https://");
        } catch (MalformedURLException | IllegalArgumentException e) {
            return false;
        }
    }
}