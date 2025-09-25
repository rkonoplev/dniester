package com.example.newsplatform.dto.response;

/**
 * DTO for representing a Term (category or tag) in API responses.
 *
 * @param id         The unique identifier of the term.
 * @param name       The display name of the term (e.g., "Technology").
 * @param vocabulary The group the term belongs to (e.g., "category").
 */
public record TermDto(Long id, String name, String vocabulary) {}