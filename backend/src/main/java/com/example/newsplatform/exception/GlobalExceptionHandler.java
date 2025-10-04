package com.example.newsplatform.exception;

import com.example.newsplatform.dto.response.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Centralized exception handler for all controllers.
 * Provides unified error response format for API clients.
 *
 * Handles:
 * - Validation errors (400)
 * - Not found (404)
 * - Access denied (403)
 * - Internal server errors (500)
 * - IllegalArgumentException (400)
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles uncaught exceptions (500 - Internal Server Error).
     *
     * Security note:
     * - Do not expose ex.getMessage() to clients (avoid leaking stacktrace/DB info).
     * - Log real error internally, return safe generic message to client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception ex, WebRequest request) {
        LOGGER.error("Unexpected error", ex);

        ErrorResponseDto error = buildErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error occurred",
                request.getDescription(false),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handles validation errors from @Valid annotated requests (400).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .toList(); // modern immutable List

        ErrorResponseDto error = buildErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getDescription(false),
                fieldErrors
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles custom not-found exceptions (404).
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(
            NotFoundException ex, WebRequest request) {

        ErrorResponseDto error = buildErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getDescription(false),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles access denied exceptions (403).
     *
     * Note: return ex.getMessage() to preserve context (instead of hardcoded text).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        ErrorResponseDto error = buildErrorResponse(
                Instant.now(),
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request.getDescription(false),
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Handles IllegalArgumentException (400).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        ErrorResponseDto error = buildErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getDescription(false),
                Collections.emptyList()
        );
        return ResponseEntity.badRequest().body(error);
    }

    // === Private Helpers ===
    private ErrorResponseDto buildErrorResponse(
            Instant timestamp, HttpStatus status, String message, String path, List<String> details) {
        return new ErrorResponseDto(timestamp, status.value(), message, path, details);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}