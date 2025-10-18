package com.example.phoebe.dto;

import com.example.phoebe.dto.response.ErrorResponseDto;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for ErrorResponseDto.
 * Tests error response structure and defensive copying.
 */
class ErrorResponseDtoTest {

    /**
     * Test basic constructor without error code.
     */
    @Test
    void constructorWithoutErrorCodeShouldSetAllFields() {
        Instant timestamp = Instant.now();
        int status = 400;
        String message = "Bad request";
        String details = "uri=/test";
        List<String> errors = List.of("field1: required", "field2: invalid");

        ErrorResponseDto dto = new ErrorResponseDto(timestamp, status, message, details, errors);

        assertEquals(timestamp, dto.getTimestamp());
        assertEquals(status, dto.getStatus());
        assertEquals(message, dto.getMessage());
        assertEquals(details, dto.getDetails());
        assertEquals(errors, dto.getErrors());
        assertNull(dto.getErrorCode());
    }

    /**
     * Test constructor with error code.
     */
    @Test
    void constructorWithErrorCodeShouldSetAllFields() {
        Instant timestamp = Instant.now();
        int status = 404;
        String message = "Not found";
        String details = "uri=/test";
        List<String> errors = List.of("Resource not found");
        String errorCode = "NOT_FOUND";

        ErrorResponseDto dto = new ErrorResponseDto(timestamp, status, message, details, errors, errorCode);

        assertEquals(timestamp, dto.getTimestamp());
        assertEquals(status, dto.getStatus());
        assertEquals(message, dto.getMessage());
        assertEquals(details, dto.getDetails());
        assertEquals(errors, dto.getErrors());
        assertEquals(errorCode, dto.getErrorCode());
    }

    /**
     * Test defensive copying of errors list.
     */
    @Test
    void errorsShouldBeDefensivelyCopied() {
        List<String> originalErrors = List.of("error1", "error2");
        ErrorResponseDto dto = new ErrorResponseDto(
            Instant.now(), 400, "Test", "uri=/test", originalErrors, "TEST_ERROR"
        );

        List<String> retrievedErrors = dto.getErrors();
        assertNotNull(retrievedErrors);
        assertEquals(originalErrors.size(), retrievedErrors.size());
        assertTrue(retrievedErrors.containsAll(originalErrors));
    }

    /**
     * Test null errors handling.
     */
    @Test
    void nullErrorsShouldBeHandledGracefully() {
        ErrorResponseDto dto = new ErrorResponseDto(
            Instant.now(), 500, "Error", "uri=/test", null, "SERVER_ERROR"
        );

        assertNull(dto.getErrors());
    }

    /**
     * Test setters work correctly.
     */
    @Test
    void settersShouldWorkCorrectly() {
        ErrorResponseDto dto = new ErrorResponseDto();
        
        Instant timestamp = Instant.now();
        dto.setTimestamp(timestamp);
        dto.setStatus(422);
        dto.setMessage("Unprocessable entity");
        dto.setDetails("uri=/validation");
        dto.setErrorCode("VALIDATION_FAILED");
        dto.setErrors(List.of("validation error"));

        assertEquals(timestamp, dto.getTimestamp());
        assertEquals(422, dto.getStatus());
        assertEquals("Unprocessable entity", dto.getMessage());
        assertEquals("uri=/validation", dto.getDetails());
        assertEquals("VALIDATION_FAILED", dto.getErrorCode());
        assertNotNull(dto.getErrors());
        assertEquals(1, dto.getErrors().size());
    }
}