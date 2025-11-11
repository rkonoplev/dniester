package com.example.phoebe.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Abstract base class for integration tests.
 * Automatically selects appropriate testing strategy:
 * - CI environment: Uses MySQL from Docker Compose
 * - Local environment: Uses Testcontainers MySQL
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("${integration.test.profile:integration-test}")
@Testcontainers
@Transactional
public abstract class AbstractIntegrationTest {

    @Container
    static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("phoebe_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Only configure if not in CI environment
        String profile = System.getProperty("integration.test.profile", "integration-test");
        if (!"ci-integration".equals(profile)) {
            registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
            registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
            registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
            registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        }
    }
}
