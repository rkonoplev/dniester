package com.example.newsplatform.dto;

import com.example.newsplatform.value.PaginationInfo;
import java.util.List;

/**
 * Generic DTO for paginated API responses.
 */
public record PagedResponse<T>(
        List<T> content,
        PaginationInfo pagination
) {}