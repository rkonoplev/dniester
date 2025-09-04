package com.example.newsplatform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Database configuration properties.
 */
@ConfigurationProperties(prefix = "app.database")
public record DatabaseProperties(
        int maxPoolSize,
        int minPoolSize,
        long connectionTimeout,
        boolean showSql
) {}