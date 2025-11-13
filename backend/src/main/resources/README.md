# Application Configuration Files

This directory contains the configuration files for the Spring Boot application. The setup uses a profile-based
system to manage different environments, ensuring flexibility and separation of concerns.

---

## Configuration Strategy

The configuration is split across multiple files, loaded based on the active Spring profile.

1.  **`application.yml` (Base)**: Contains common properties that apply to all environments, such as API paths,
    port numbers, and default JPA settings.

2.  **`application-{profile}.yml` (Specific Overrides)**: Contains properties that override the base configuration
    for a specific profile. For example, `application-local.yml` provides settings for local development.

The active profiles are enabled via the `spring.profiles.active` property, typically passed as a command-line
argument or in an environment variable.

---

## File Descriptions

-   **`application.yml`**
    -   **Purpose**: The main, default configuration file. It defines the base for all other profiles.
    -   **Key Settings**: Default server port, application name, base JPA configurations.

-   **`application-local.yml`**
    -   **Purpose**: The primary profile for **local development**, activated by `make run`.
    -   **Key Settings**: Configures the datasource for the Docker Compose MySQL container, enables Flyway, sets `ddl-auto` to `update`, and enables detailed SQL logging.

-   **`application-integration-test.yml`**
    -   **Purpose**: Used for all integration tests in all environments (local and CI), activated by `make test`.
    -   **Key Settings**: Configured for Testcontainers MySQL. Uses `create-drop` schema management and disables Flyway, as Hibernate manages the schema directly for tests.

-   **`application-prod.yml`**
    -   **Purpose**: The profile for **production** environments.
    -   **Key Settings**: Disables detailed SQL logging and sets `ddl-auto` to `validate`. Relies on environment variables for all sensitive data.

-   **`application-mysql.yml`** & **`application-postgresql.yml`**
    -   **Purpose**: Database-specific profiles that provide the correct JDBC driver, Hibernate dialect, and Flyway migration paths.

-   **`application-security.yml`**
    -   **Purpose**: Centralizes all security-related configurations (Basic Auth, rate limiting). It is automatically included by `application.yml`.
