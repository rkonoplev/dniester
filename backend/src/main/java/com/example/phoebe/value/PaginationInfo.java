package com.example.phoebe.value;

/**
 * Value object for pagination information.
 */
public record PaginationInfo(
        int currentPage,
        int totalPages,
        long totalElements,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious
) {}