package com.example.phoebe.dto.response;

import com.example.phoebe.value.PaginationInfo;
import java.util.List;

/**
 * Generic DTO for paginated API responses.
 */
public record PagedResponseDto<T>(
        List<T> content,
        PaginationInfo pagination
) {}