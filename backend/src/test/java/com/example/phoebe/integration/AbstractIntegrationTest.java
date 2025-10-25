package com.example.phoebe.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Abstract base class for integration tests that require a real database.
 * This class uses Testcontainers to spin up a MySQL Docker container for each test run,
 * ensuring a clean, isolated environment.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration-test")
public abstract class AbstractIntegrationTest {

    /**
     * Defines the MySQL Docker container. The image version should match the one used in production.
     * The container is static, meaning it will be started once for all tests in the class that extends this base class.
     */
    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.3.0");

    /**
     * Dynamically configures the Spring Boot application properties to connect to the Testcontainer.
     * This method overrides the datasource properties at runtime, pointing them to the dynamically
     * allocated port and credentials of the Docker container.
     *
     * @param registry The dynamic property registry.
     */
    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }
}
