# Test Application Configuration

This directory contains configuration files that are used exclusively when running tests. These files override
properties from the main `src/main/resources` directory to create a controlled and isolated test environment.

Spring Boot uses a profile-based system for test configurations. This allows us to have different setups for
different types of tests (e.g., fast unit tests vs. slower integration tests).

---

## `application-test.yml` (Default Test Profile)

This is the **default profile** used for most tests, especially fast unit and slice tests like `@WebMvcTest`
and `@DataJpaTest`. It is automatically activated when no other profile is specified.

Its main purpose is to use a fast, in-memory database to avoid the overhead of starting a Docker container.

### Key Properties:

- **`spring.datasource.url: jdbc:h2:mem:testdb;...`**: Configures an **H2 in-memory database**.
  The `MODE=MYSQL` parameter ensures better compatibility with MySQL syntax.

- **`spring.jpa.hibernate.ddl-auto: create-drop`**: Creates the schema at the start of the test run and drops it
  at the end. This is the standard, safe approach for in-memory databases.

---

## `application-integration-test.yml` (Integration Test Profile)

This profile is activated specifically for full integration tests (annotated with `@SpringBootTest` and
`@ActiveProfiles("integration-test")`) that require a real database environment.

### Key Properties:

- **`spring.jpa.hibernate.ddl-auto: create`**: This setting tells Hibernate to create the schema at the start
  of the test run but **not** to drop it at the end. This prevents `Communications link failure` errors that
  occur when the Testcontainers database is shut down before Hibernate can act.

- **`spring.datasource.url: jdbc:tc:mysql:8.0:///dniester`**: This special URL instructs Spring Boot to use
  **Testcontainers** to automatically spin up a temporary MySQL 8.0 Docker container for the duration of the
  test run. This ensures tests run against a clean, ephemeral, and isolated database every time.