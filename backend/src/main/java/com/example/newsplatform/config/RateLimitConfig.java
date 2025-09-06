package com.example.newsplatform.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting configuration using Bucket4j.
 * Provides different rate limits for public and admin APIs.
 */
@Configuration
public class RateLimitConfig {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Get or create bucket for IP address with public API limits.
     * Limit: 100 requests per minute.
     */
    public Bucket getPublicBucket(String ipAddress) {
        return buckets.computeIfAbsent("public:" + ipAddress, key -> 
            Bucket.builder()
                .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
                .build()
        );
    }

    /**
     * Get or create bucket for IP address with admin API limits.
     * Limit: 50 requests per minute (more restrictive).
     */
    public Bucket getAdminBucket(String ipAddress) {
        return buckets.computeIfAbsent("admin:" + ipAddress, key -> 
            Bucket.builder()
                .addLimit(Bandwidth.classic(50, Refill.intervally(50, Duration.ofMinutes(1))))
                .build()
        );
    }
}