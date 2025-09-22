package com.example.newsplatform.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CacheConfig.class})
class CacheConfigTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    void cacheManager_ShouldBeConfigured() {
        assertNotNull(cacheManager, "CacheManager should be configured");
    }

    @Test
    void cacheManager_ShouldCreateCaches() {
        assertNotNull(cacheManager.getCache("news-by-id"), "'news-by-id' cache should exist");
        assertNotNull(cacheManager.getCache("news-by-term"), "'news-by-term' cache should exist");
    }

    @Test
    void cache_ShouldStoreAndRetrieveValues() {
        Cache cache = cacheManager.getCache("news-by-id");
        assertNotNull(cache);

        String key = "testKey";
        String value = "testValue";

        cache.put(key, value);
        String retrievedValue = cache.get(key, String.class);

        assertEquals(value, retrievedValue, "Should retrieve the stored value from the cache");
    }
}
