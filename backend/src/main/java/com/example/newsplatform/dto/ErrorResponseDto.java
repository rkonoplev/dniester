package com.example.newsplatform.dto;

import java.time.Instant;
import java.util.List;

/**
 * DTO representing a standardized error response returned by the API.
 * Used by GlobalExceptionHandler across all controllers.
 */
public class ErrorResponseDto {

    private Instant timestamp;
    private int status;
    private String message;
    private String details;
    private List<String> errors; // optional, for validation errors

    public ErrorResponseDto() {}

    public ErrorResponseDto(Instant timestamp, int status, String message, String details, List<String> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.details = details;
        this.errors = errors;
    }

    // --- Getters & Setters ---
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}