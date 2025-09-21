package com.example.newsplatform.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CacheConfig.
 * Tests cache manager configuration and cache creation.
 */
@SpringBootTest
@TestPropertySource(properties = "spring.cache.type=caffeine")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CacheConfigTest {

    @Autowired
    private CacheManager cacheManager;

    /**
     * Test that cache manager is properly configured and available.
     */
    @Test
    void cacheManager_ShouldBeConfigured() {
        assertNotNull(cacheManager);
        assertEquals("CaffeineCacheManager", cacheManager.getClass().getSimpleName());
    }

    /**
     * Test that caches can be created and accessed.
     */
    @Test
    void cacheManager_ShouldCreateCaches() {
        // Test cache creation
        assertNotNull(cacheManager.getCache("terms"));
        assertNotNull(cacheManager.getCache("publishedNews"));
        assertNotNull(cacheManager.getCache("newsByTerm"));
    }

    /**
     * Test cache operations - put and get.
     */
    @Test
    void cache_ShouldStoreAndRetrieveValues() {
        var cache = cacheManager.getCache("testCache");
        assertNotNull(cache);

        // Test cache put and get
        cache.put("key1", "value1");
        assertEquals("value1", cache.get("key1", String.class));

        // Test cache miss
        assertNull(cache.get("nonexistent", String.class));
    }
}
