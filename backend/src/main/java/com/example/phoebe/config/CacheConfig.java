package com.example.phoebe.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Cache configuration using Caffeine for high-performance in-memory caching.
 * Optimizes frequently accessed data like terms, published news, and search results.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure Caffeine cache manager with optimized settings for news platform.
     * 
     * Cache Strategy:
     * - Terms: Long TTL (1 hour) - rarely change, frequently accessed
     * - Published News: Medium TTL (15 min) - balance freshness vs performance  
     * - Search Results: Short TTL (5 min) - dynamic content, frequent updates
     * 
     * @return configured cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Default cache configuration for most use cases
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)                    // Max 1000 entries per cache
                .expireAfterWrite(Duration.ofMinutes(15))  // Default 15 min TTL
                .recordStats());                      // Enable metrics for monitoring
        
        return cacheManager;
    }

    /**
     * Specialized cache for taxonomy terms (categories/tags).
     * Longer TTL since terms change infrequently.
     */
    @Bean
    public Caffeine<Object, Object> termsCaffeineConfig() {
        return Caffeine.newBuilder()
                .maximumSize(500)                     // Smaller cache for terms
                .expireAfterWrite(Duration.ofHours(1))     // 1 hour TTL
                .recordStats();
    }

    /**
     * Cache for search results with shorter TTL for data freshness.
     */
    @Bean  
    public Caffeine<Object, Object> searchCaffeineConfig() {
        return Caffeine.newBuilder()
                .maximumSize(2000)                    // Larger cache for search variety
                .expireAfterWrite(Duration.ofMinutes(5))   // 5 min TTL for freshness
                .recordStats();
    }
}