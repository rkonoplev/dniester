package com.example.newsplatform.dto;

import com.example.newsplatform.value.PaginationInfo;
import java.util.List;

/**
 * Generic DTO for paginated API responses.
 */
public record PagedResponseDto<T>(
        List<T> content,
        PaginationInfo pagination
) {}