package com.example.newsplatform.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception thrown when an entity is not found in the database.
 */
public class NotFoundException extends BaseException {
    
    public NotFoundException(String message) {
        super(message, "NOT_FOUND", HttpStatus.NOT_FOUND);
    }
    
    public NotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue),
              "NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}