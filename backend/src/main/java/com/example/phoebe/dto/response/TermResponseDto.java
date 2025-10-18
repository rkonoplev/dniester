package com.example.phoebe.dto.response;

/**
 * DTO for representing a Term (category or tag) in API responses.
 * This record is immutable and provides a clean data structure for the API.
 *
 * @param id The unique identifier of the term.
 * @param name The display name of the term (e.g., "Technology").
 * @param vocabulary The group the term belongs to (e.g., "category").
 */
public record TermResponseDto(Long id, String name, String vocabulary) {
}