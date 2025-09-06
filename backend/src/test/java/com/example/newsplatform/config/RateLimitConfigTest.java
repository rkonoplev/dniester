package com.example.newsplatform.config;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitConfigTest {

    private RateLimitConfig rateLimitConfig;

    @BeforeEach
    void setUp() {
        rateLimitConfig = new RateLimitConfig();
    }

    @Test
    void getPublicBucket_ShouldReturnSameBucketForSameIP() {
        String ipAddress = "192.168.1.1";
        
        Bucket bucket1 = rateLimitConfig.getPublicBucket(ipAddress);
        Bucket bucket2 = rateLimitConfig.getPublicBucket(ipAddress);
        
        assertSame(bucket1, bucket2);
    }

    @Test
    void getPublicBucket_ShouldReturnDifferentBucketsForDifferentIPs() {
        String ip1 = "192.168.1.1";
        String ip2 = "192.168.1.2";
        
        Bucket bucket1 = rateLimitConfig.getPublicBucket(ip1);
        Bucket bucket2 = rateLimitConfig.getPublicBucket(ip2);
        
        assertNotSame(bucket1, bucket2);
    }

    @Test
    void getAdminBucket_ShouldReturnSameBucketForSameIP() {
        String ipAddress = "192.168.1.1";
        
        Bucket bucket1 = rateLimitConfig.getAdminBucket(ipAddress);
        Bucket bucket2 = rateLimitConfig.getAdminBucket(ipAddress);
        
        assertSame(bucket1, bucket2);
    }

    @Test
    void getAdminBucket_ShouldReturnDifferentBucketsForDifferentIPs() {
        String ip1 = "192.168.1.1";
        String ip2 = "192.168.1.2";
        
        Bucket bucket1 = rateLimitConfig.getAdminBucket(ip1);
        Bucket bucket2 = rateLimitConfig.getAdminBucket(ip2);
        
        assertNotSame(bucket1, bucket2);
    }

    @Test
    void getPublicAndAdminBuckets_ShouldReturnDifferentBucketsForSameIP() {
        String ipAddress = "192.168.1.1";
        
        Bucket publicBucket = rateLimitConfig.getPublicBucket(ipAddress);
        Bucket adminBucket = rateLimitConfig.getAdminBucket(ipAddress);
        
        assertNotSame(publicBucket, adminBucket);
    }

    @Test
    void publicBucket_ShouldHave100TokensInitially() {
        String ipAddress = "192.168.1.1";
        
        Bucket bucket = rateLimitConfig.getPublicBucket(ipAddress);
        
        assertEquals(100, bucket.getAvailableTokens());
    }

    @Test
    void adminBucket_ShouldHave50TokensInitially() {
        String ipAddress = "192.168.1.1";
        
        Bucket bucket = rateLimitConfig.getAdminBucket(ipAddress);
        
        assertEquals(50, bucket.getAvailableTokens());
    }

    @Test
    void publicBucket_ShouldConsumeTokens() {
        String ipAddress = "192.168.1.1";
        
        Bucket bucket = rateLimitConfig.getPublicBucket(ipAddress);
        boolean consumed = bucket.tryConsume(1);
        
        assertTrue(consumed);
        assertEquals(99, bucket.getAvailableTokens());
    }

    @Test
    void adminBucket_ShouldConsumeTokens() {
        String ipAddress = "192.168.1.1";
        
        Bucket bucket = rateLimitConfig.getAdminBucket(ipAddress);
        boolean consumed = bucket.tryConsume(1);
        
        assertTrue(consumed);
        assertEquals(49, bucket.getAvailableTokens());
    }
}