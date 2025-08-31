package com.example.newsplatform.exception;

import com.example.newsplatform.dto.ErrorResponseDto;
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
import java.util.stream.Collectors;

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

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles uncaught exceptions (500 - Internal Server Error).
     *
     * Security note:
     * - We do NOT expose ex.getMessage() to clients to avoid leaking stacktrace/db details.
     * - Return a generic message to clients, log real error internally.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception ex, WebRequest request) {
        // log full technical details
        logger.error("Unexpected error occurred", ex);

        ErrorResponseDto error = buildErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error occurred",   // <--- safe generic message
                request.getDescription(false),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles validation errors from @Valid annotated requests (400 - Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());

        ErrorResponseDto error = buildErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request.getDescription(false),
                fieldErrors
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles custom not-found exceptions (404 - Not Found).
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
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles access denied exceptions (403 - Forbidden).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        ErrorResponseDto error = buildErrorResponse(
                Instant.now(),
                HttpStatus.FORBIDDEN,
                "Access is denied",
                request.getDescription(false),
                null
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles illegal arguments thrown manually in service layer (400 - Bad Request).
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
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // === Private Helpers ===

    /**
     * Builds a standardized error response DTO.
     */
    private ErrorResponseDto buildErrorResponse(
            Instant timestamp,
            HttpStatus status,
            String message,
            String path,
            List<String> details) {
        return new ErrorResponseDto(
                timestamp,
                status.value(),
                message,
                path,
                details
        );
    }

    /**
     * Formats a field error as "fieldName: message".
     */
    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}