package com.example.newsplatform.exception;

/**
 * Custom exception thrown when an entity is not found in the database.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}