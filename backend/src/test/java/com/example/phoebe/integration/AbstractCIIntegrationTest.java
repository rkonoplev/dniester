package com.example.phoebe.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract base class for integration tests in CI environment.
 * Uses MySQL from Docker Compose instead of Testcontainers.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("ci-integration")
@Transactional
public abstract class AbstractCIIntegrationTest {
}