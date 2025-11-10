# Application Configuration Files

This directory contains the configuration files for the Spring Boot application. The setup uses a profile-based
system to manage different environments, ensuring flexibility and separation of concerns.

---

## Configuration Strategy

The configuration is split across multiple files, loaded based on the active Spring profile.

1.  **`application.yml` (Base)**: Contains common properties that apply to all environments, such as API paths,
    port numbers, and default JPA settings.

2.  **`application-{profile}.yml` (Specific Overrides)**: Contains properties that override the base configuration
    for a specific profile. For example, `application-mysql.yml` provides settings specific to MySQL.

3.  **`application-security.yml`**: A dedicated file for security-related properties (e.g., JWT secrets, CORS),
    which is always included.

The active profiles are enabled via the `spring.profiles.active` property, typically passed as a command-line
argument or in an environment variable.

---

## File Descriptions

-   **`application.yml`**
    -   **Purpose**: The main, default configuration file. It defines the base for all other profiles.
    -   **Key Settings**: Default server port, application name, base JPA configurations (like open-in-view),
        and includes the `security` profile by default.

-   **`application-local.yml`**
    -   **Purpose**: The primary profile for **local development**. It enables features useful for debugging.
    -   **Key Settings**:
        -   Enables Flyway (`flyway.enabled: true`).
        -   Disables automatic schema generation (`ddl-auto: none`), giving control to Flyway.
        -   Enables detailed SQL logging (`show-sql: true`, `format_sql: true`) for performance analysis.

-   **`application-ci.yml`**
    -   **Purpose**: Used exclusively during Continuous Integration (CI) builds, like on GitHub Actions.
    -   **Key Settings**: Uses H2 in-memory database (`jdbc:h2:mem:testdb`) with `create-drop` schema
        management. Flyway is disabled since H2 creates schema automatically. Optimized for fast CI execution.

-   **`application-mysql.yml`** & **`application-postgresql.yml`**
    -   **Purpose**: Database-specific profiles. They provide the correct JDBC driver and Hibernate dialect
        for either MySQL or PostgreSQL. One of these should be activated alongside another profile (like `local`).

-   **`application-security.yml`**
    -   **Purpose**: Centralizes all security-related configurations.
    -   **Key Settings**: JWT token expiration times, secret keys (often externalized), CORS policies, and
        paths excluded from security filters. It is automatically included by `application.yml`.
