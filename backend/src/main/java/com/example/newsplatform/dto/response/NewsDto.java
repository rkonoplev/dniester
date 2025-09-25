package com.example.newsplatform.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for representing a news article in API responses.
 * This record is immutable and serves as a data carrier.
 * The teaser is optional and will not be included in the JSON if null.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NewsDto(
        Long id,
        String title,
        String body, // Full content, for the main article view
        String teaser, // Optional summary, for list views
        LocalDateTime publicationDate,
        boolean published,
        Long authorId,
        String authorName,
        Set<String> termNames
) {}