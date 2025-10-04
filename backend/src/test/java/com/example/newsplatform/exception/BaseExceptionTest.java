package com.example.newsplatform.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for BaseException and its subclasses.
 * Tests consistent error handling with HTTP status codes and error codes.
 */
class BaseExceptionTest {

    /**
     * Test NotFoundException inherits from BaseException correctly.
     */
    @Test
    void notFoundExceptionShouldHaveCorrectProperties() {
        String message = "User not found";
        NotFoundException exception = new NotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertEquals("NOT_FOUND", exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    /**
     * Test NotFoundException with resource details.
     */
    @Test
    void notFoundExceptionWithResourceDetailsShouldFormatMessage() {
        NotFoundException exception = new NotFoundException("User", "id", 123);

        assertEquals("User not found with id: '123'", exception.getMessage());
        assertEquals("NOT_FOUND", exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    /**
     * Test ValidationException has correct properties.
     */
    @Test
    void validationExceptionShouldHaveCorrectProperties() {
        String message = "Invalid input data";
        ValidationException exception = new ValidationException(message);

        assertEquals(message, exception.getMessage());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    /**
     * Test ValidationException with cause.
     */
    @Test
    void validationExceptionWithCauseShouldPreserveCause() {
        String message = "Validation failed";
        Throwable cause = new IllegalArgumentException("Invalid format");
        ValidationException exception = new ValidationException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("VALIDATION_ERROR", exception.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    /**
     * Test BusinessException has correct properties.
     */
    @Test
    void businessExceptionShouldHaveCorrectProperties() {
        String message = "Business rule violation";
        BusinessException exception = new BusinessException(message);

        assertEquals(message, exception.getMessage());
        assertEquals("BUSINESS_ERROR", exception.getErrorCode());
        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
    }

    /**
     * Test BusinessException with cause.
     */
    @Test
    void businessExceptionWithCauseShouldPreserveCause() {
        String message = "Cannot delete published article";
        Throwable cause = new IllegalStateException("Article is published");
        BusinessException exception = new BusinessException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals("BUSINESS_ERROR", exception.getErrorCode());
        assertEquals(HttpStatus.CONFLICT, exception.getHttpStatus());
    }

    /**
     * Test that all exceptions are RuntimeExceptions.
     */
    @Test
    void allExceptionsShouldBeRuntimeExceptions() {
        NotFoundException notFound = new NotFoundException("Not found");
        ValidationException validation = new ValidationException("Invalid");
        BusinessException business = new BusinessException("Business error");

        assertNotNull(notFound);
        assertNotNull(validation);
        assertNotNull(business);
        
        // Verify they can be thrown without checked exception handling
        assertEquals(RuntimeException.class, notFound.getClass().getSuperclass().getSuperclass());
        assertEquals(RuntimeException.class, validation.getClass().getSuperclass().getSuperclass());
        assertEquals(RuntimeException.class, business.getClass().getSuperclass().getSuperclass());
    }
}