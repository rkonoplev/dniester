package com.example.phoebe.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract base class for integration tests.
 * Uses external MySQL from Docker Compose in CI environment.
 * For local development with Testcontainers, extend LocalIntegrationTest instead.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("${integration.test.profile:ci}")
@Transactional
public abstract class AbstractIntegrationTest {
    // Uses external MySQL from Docker Compose
}
