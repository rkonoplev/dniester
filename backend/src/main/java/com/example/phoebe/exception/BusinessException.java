package com.example.phoebe.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when business logic rules are violated.
 */
public class BusinessException extends BaseException {
    
    public BusinessException(String message) {
        super(message, "BUSINESS_ERROR", HttpStatus.CONFLICT);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, "BUSINESS_ERROR", HttpStatus.CONFLICT, cause);
    }
}