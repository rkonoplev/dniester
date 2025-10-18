package com.example.phoebe.value;

import java.util.Set;

/**
 * Value object for bulk operation results.
 */
public record BulkOperationResult(
        int affectedCount,
        Set<Long> processedIds,
        Set<Long> failedIds,
        String message
) {}