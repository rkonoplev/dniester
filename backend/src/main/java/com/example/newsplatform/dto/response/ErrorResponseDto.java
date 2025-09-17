package com.example.newsplatform.dto.response;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * DTO representing a standardized error response.
 * Returned by GlobalExceptionHandler across all controllers.
 */
public class ErrorResponseDto {

    private Instant timestamp;
    private int status;
    private String message;
    private String details;
    private List<String> errors;

    public ErrorResponseDto() {}

    public ErrorResponseDto(Instant timestamp, int status, String message, String details, List<String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.details = details;
        // defensive copy
        this.errors = errors != null ? List.copyOf(errors) : null;
    }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    /** Returns unmodifiable list to prevent external modification. */
    public List<String> getErrors() {
        return errors != null ? Collections.unmodifiableList(errors) : null;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors != null ? List.copyOf(errors) : null;
    }
}