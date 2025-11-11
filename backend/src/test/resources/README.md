# Test Application Configuration

This directory contains configuration files that are used exclusively when running tests. These files override
properties from the main `src/main/resources` directory to create a controlled and isolated test environment.

Spring Boot uses a profile-based system for test configurations. This allows us to have different setups for
different types of tests (unit tests vs. integration tests).

---

## `application-test.yml` (Unit Test Profile)

This is the **default profile** used for unit tests that don't require a database connection.
Unit tests use mocks and don't load the full Spring context.

### Key Properties:

- **`spring.jpa.hibernate.ddl-auto: validate`**: Validates the schema without creating or dropping tables.
- **`spring.jpa.database-platform: org.hibernate.dialect.MySQLDialect`**: Uses MySQL dialect for consistency.
- **`spring.flyway.enabled: true`**: Enables Flyway for schema management.
- **`spring.flyway.locations: classpath:db/migration/common,classpath:db/migration/mysql`**: Specifies migration paths.

---

## `application-integration-test.yml` (Integration Test Profile)

This profile is activated specifically for full integration tests (annotated with `@SpringBootTest` and
`@ActiveProfiles("integration-test")`) that require a real database environment using Testcontainers.

### Key Properties:

- **`spring.jpa.hibernate.ddl-auto: validate`**: Validates the schema managed by Flyway.
- **`spring.jpa.database-platform: org.hibernate.dialect.MySQLDialect`**: Uses MySQL dialect.
- **`spring.flyway.enabled: true`**: Enables Flyway for schema management.
- **`spring.flyway.locations: classpath:db/migration/common,classpath:db/migration/mysql`**: Specifies migration paths.
- **`spring.sql.init.mode: never`**: Prevents Spring from initializing the database (Flyway handles this).

**Note**: Database connection is configured dynamically by Testcontainers in the `AbstractIntegrationTest` class.