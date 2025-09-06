package com.example.newsplatform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Database configuration properties.
 * 
 * LEGACY FILE - Used during Drupal 6 migration process.
 * This class was created to handle custom database connection settings
 * during the migration from Drupal 6 to News Platform.
 * 
 * Migration usage:
 * - Custom connection pool sizes for handling large data transfers
 * - Extended connection timeouts for migration scripts
 * - SQL logging for debugging migration queries
 * 
 * Status: OBSOLETE - Migration completed, standard Spring datasource config used
 * Date: Migration completed in 2024
 */
@ConfigurationProperties(prefix = "app.database")
public record DatabaseProperties(
        int maxPoolSize,
        int minPoolSize,
        long connectionTimeout,
        boolean showSql
) {}