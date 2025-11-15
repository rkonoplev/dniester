# Configuration Guide

> For key terms and technologies, please refer to the **[Glossary](./GLOSSARY.md)**.

## Table of Contents
- [Configuration Files Location](#configuration-files-location)
- [Test Configuration](#test-configuration)
- [Spring Profiles Matrix](#spring-profiles-matrix)
- [Running with Profiles](#running-with-profiles)
- [Environment Variables & .env](#environment-variables--env)
- [Secrets Management](#secrets-management)
- [Best Practices](#best-practices)


This document explains the configuration strategy for the Phoebe CMS backend.  
It uses Spring Boot profiles, YAML files, and environment variables to ensure security, portability, and consistency across different environments.

---

## Configuration Files Location

The application configuration is split between two main directories:

1.  **`backend/src/main/resources/`**
    - Contains configurations for running the main application, as well as the main test configuration file.
    - `application.yml` is the base file.
    - `application-<profile>.yml` files provide environment-specific settings.

2.  **`backend/src/test/resources/`**
    - May contain files to override configurations from `main/resources` specifically for tests.
    - Settings from this directory have a **higher priority** during a test run.

---

## Test Configuration

- **`application-integration-test.yml` (Current Profile)**
  - **Status**: **The only profile used for all tests.**
  - **Location**: `src/main/resources/`
  - **Description**: Used for all integration tests (`@SpringBootTest`). It is activated via `@ActiveProfiles("integration-test")`. It is configured to use **Testcontainers** with MySQL. Flyway is disabled in this profile (`spring.flyway.enabled: false`), and the schema is managed by Hibernate (`ddl-auto: create-drop`).

- **`application-test.yml` (Legacy Profile)**
  - **Status**: **Legacy, not in use.**
  - **Location**: `src/test/resources/`
  - **Description**: Was previously the default profile for tests and was configured to use the H2 in-memory database. After the full migration to Testcontainers, this profile is no longer part of the main testing workflow.

---

## Spring Profiles Matrix

| Profile            | Status                               | File Location      | Database         | Usage                                                               |
|:-------------------|:-------------------------------------|:-------------------|:-------------------|:--------------------------------------------------------------------|
| `local`            | **Current**                          | `main/resources`   | MySQL (Docker)     | Local development via `make run`.                                   |
| `integration-test` | **Current (for tests)**              | `main/resources`   | MySQL (Testcontainers) | All tests (`make test`). Testcontainers manages the DB.             |
| `prod`             | **Current**                          | `main/resources`   | External DB        | Production build.                                                   |
| `test`             | **Legacy**                           | `test/resources`   | H2 (In-Memory)   | Previously used for H2-based tests. **No longer in use.**           |
| `dev`              | **Legacy**                           | `legacy`           | MySQL (Docker)     | Previously used for dev environments. **No longer in use.**         |
| `ci`               | **Legacy**                           | `legacy`           | H2 (In-Memory)   | Previously used in CI. **No longer in use.**                        |
| `mysql` / `postgresql` | Helper                           | `main/resources`   | -                  | Specify paths to DB-specific Flyway migrations.                     |
| `security`         | Current (included automatically)     | `main/resources`   | -                  | Centralizes security settings.                                      |

---

## Running with Profiles

**Current Methods:**
```bash
# Run for local development (activates the 'local' profile)
make run

# Run all tests (activates the 'integration-test' profile)
make all-tests
```

**Legacy Examples (for historical context):**
```bash
# (Legacy Example) Run with a combined dev and mysql profile
SPRING_PROFILES_ACTIVE=dev,mysql ./gradlew bootRun

# (Legacy Example) Run tests with the CI profile
SPRING_PROFILES_ACTIVE=ci ./gradlew test
```

---

## Environment Variables & .env

The `.env` file (ignored by git) provides credentials for the database and application locally when using `make run`.

Example `.env`:
```
# MySQL credentials
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=phoebe_db
MYSQL_USER=root
MYSQL_PASSWORD=root

# Ports
SPRING_LOCAL_PORT=8080
DATABASE_LOCAL_PORT=3306

# Spring datasource
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:${DATABASE_LOCAL_PORT}/${MYSQL_DATABASE}
SPRING_DATASOURCE_USERNAME=${MYSQL_USER}
SPRING_DATASOURCE_PASSWORD=${MYSQL_PASSWORD}

# Authentication credentials (Basic Auth)
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin
```

---

## Secrets Management

### Authentication Security
- **Current Implementation**: Basic Auth with database-backed user credentials.
- **Role Separation**: ADMIN (full access), EDITOR (content management).
- **BCrypt Encoding** for password security.

### Environment Management
- **Locally**: `.env` file (git-ignored).
- **CI/CD**: GitHub Actions → Repository Secrets.
- **Production**: Environment variables or secret files.

---

## Best Practices
- Keep `application.yml` for minimal, non-sensitive defaults only.
- Use `.env` for local development only; commit `.env.example` as a template.
- Production configurations (the `prod` profile) should read data exclusively from environment variables.
- Align the database schema strategy for each environment: `update` locally, `validate` in production.