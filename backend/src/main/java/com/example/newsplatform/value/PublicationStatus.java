package com.example.newsplatform.value;

import java.time.LocalDateTime;

/**
 * Value object representing publication status and timing.
 */
public record PublicationStatus(
        boolean published,
        LocalDateTime scheduledDate,
        String status
) {
    public static PublicationStatus draft() {
        return new PublicationStatus(false, null, "DRAFT");
    }
    
    public static PublicationStatus published(LocalDateTime date) {
        return new PublicationStatus(true, date, "PUBLISHED");
    }
    
    public static PublicationStatus scheduled(LocalDateTime date) {
        return new PublicationStatus(false, date, "SCHEDULED");
    }
}