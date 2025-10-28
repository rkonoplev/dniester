# Main Application Configuration

This directory contains the primary configuration files for the Spring Boot application. These files define how the
application behaves when it runs in a development or production environment.

Spring Boot uses a profile-based system to manage configurations. The base configuration is in `application.yml`,
and profile-specific properties (e.g., for `dev` or `prod`) are defined in `application-{profile}.yml` files.

---

## `application.yml` (Base Configuration)

This is the main, default configuration file for the Phoebe CMS backend. It contains properties that are common
across all environments and are used by default during local development.

### Key Properties:

- **`spring.jpa.hibernate.ddl-auto: create-drop`**: This setting instructs Hibernate to automatically create the
  database schema when the application starts and drop it when it shuts down. This is useful for development,
  as it ensures a clean database on every restart.

- **`spring.datasource.*`**: These properties configure the connection to the primary MySQL database. The URL,
  username, and password point to the database instance (e.g., a local Docker container) that the application
  uses for its data persistence.

---

## `application-prod.yml` (Production Profile Example)

A file like this would contain overrides for the production environment. When the application is run with the `prod`
profile active (e.g., via `-Dspring.profiles.active=prod`), properties in this file take precedence.

### Example Overrides:

- **`spring.jpa.hibernate.ddl-auto: validate`**: In production, this would be set to `validate` or `none` to
  prevent accidental data loss.
- **`spring.datasource.url`**: The URL would be updated to point to the production database instance.