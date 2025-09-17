package com.example.newsplatform.exception;

import com.example.newsplatform.dto.response.ErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalExceptionHandler.
 * Tests centralized exception handling across all controllers.
 * Verifies proper HTTP status codes and error response formatting.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/test");
    }

    /**
     * Test handling of unexpected runtime exceptions.
     * Should return HTTP 500 with generic error message for security.
     */
    @Test
    void handleAllExceptions_ShouldReturn500() {
        Exception exception = new RuntimeException("Test error");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleAllExceptions(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error occurred", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
    }

    /**
     * Test handling of validation errors from @Valid annotations.
     * Should return HTTP 400 with detailed field error messages.
     */
    @Test
    void handleValidationExceptions_ShouldReturn400() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "Field is required");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleValidationExceptions(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertTrue(response.getBody().getErrors().contains("field: Field is required"));
    }

    /**
     * Test handling of custom NotFoundException.
     * Should return HTTP 404 with specific error message.
     */
    @Test
    void handleNotFoundException_ShouldReturn404() {
        NotFoundException exception = new NotFoundException("Resource not found");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleNotFoundException(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
    }

    /**
     * Test handling of Spring Security access denied exceptions.
     * Should return HTTP 403 with access denied message.
     */
    @Test
    void handleAccessDeniedException_ShouldReturn403() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleAccessDeniedException(exception, webRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied", response.getBody().getMessage());
        assertEquals(403, response.getBody().getStatus());
    }

    /**
     * Test handling of illegal argument exceptions.
     * Should return HTTP 400 with specific error details.
     */
    @Test
    void handleIllegalArgumentException_ShouldReturn400() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleIllegalArgumentException(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
    }
}