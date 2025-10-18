package com.example.phoebe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security configuration properties.
 */
@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
        String jwtSecret,
        long jwtExpirationMs,
        boolean enableCsrf,
        String[] allowedOrigins
) {}