package com.example.phoebe.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract base class for integration tests.
 * - Local development: Uses 'test' profile (H2 in-memory)
 * - CI environment: Uses 'ci' profile when SPRING_PROFILES_ACTIVE=ci is set
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"${spring.profiles.active:test}"})
@Transactional
public abstract class AbstractIntegrationTest {
}
