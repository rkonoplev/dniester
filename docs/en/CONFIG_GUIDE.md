# Configuration Guide

> For key terms and technologies, please refer to the **[Glossary](./GLOSSARY_EN.md)**.

## Table of Contents
- [Configuration Files Location](#configuration-files-location)
- [Test Configuration](#test-configuration)
- [Spring Profiles Matrix](#spring-profile-matrix)
- [Running with Profiles](#running-with-profiles)
- [Environment Variables & .env](#environment-variables--env)
- [Secrets Management](#secrets-management)
- [Best Practices](#best-practices)


This document explains the configuration strategy for the Phoebe CMS backend. It uses Spring Boot profiles,
YAML files, and environment variables to ensure security, portability, and consistency across different
environments.

---

## Configuration Files Location

The application configuration is split between two main directories:

1.  **`backend/src/main/resources/`**
    - Contains configurations for running the main application (local, CI, production).
    - `application.yml` is the base file, containing settings common to all environments.
    - `application-<profile>.yml` files provide specific settings for each environment, overriding the
      base file.

2.  **`backend/src/test/resources/`**
    - Contains configurations **only for running tests**.
    - Settings from this directory have a **higher priority** and override files with the same name from
      `main/resources` during a test run.

Spring Boot selects the configuration based on the active profile (`SPRING_PROFILES_ACTIVE`).

---

## Test Configuration

Tests use a separate set of configuration files in `src/test/resources/` to isolate the test environment.

- **`application.yml`** (in `test`) is the base file for all tests. It overrides the main `application.yml` and
  typically contains logging settings and other common test parameters.
- **`application-test.yml`** is used by default for unit tests that don't require database access.
- **Integration tests** use Testcontainers with real MySQL instances:
  - **Unit tests**: Use `test` profile with mocks, no database dependencies
  - **Integration tests**: Use `integration-test` profile with Testcontainers MySQL
  - **CI environment**: Uses MySQL via Docker Compose for production-like testing
  - All database tests use the same migrations from `common/` and `mysql/` as production
  - Tests have access to test data from `V3__insert_sample_data.sql`
  - Profile selection is handled by `AbstractIntegrationTest` base class

---

## Spring Profile Matrix

| Profile            | File                               | Database         | Schema Strategy | Usage                                                               |
|:-------------------|:-----------------------------------|:-----------------|:----------------|:--------------------------------------------------------------------|
| `local`            | `application-local.yml`            | MySQL (Docker)   | `update`        | Local dev with `docker-compose`; uses `.env` for DB credentials.    |
| `dev`              | `application-dev.yml`              | Vendor-Specific  | `update`        | Dev/staging; combined with `mysql` or `postgresql` profile.         |
| `test`             | `application-test.yml`             | None (Mocks)     | `validate`      | **(Tests only)** Unit tests with mocks, no database dependencies. Used by default. |
| `integration-test` | `application-integration-test.yml` | MySQL (Testcontainers) | `validate` | **(Tests only)** Integration tests with real MySQL via Testcontainers. |
| `ci`               | `application-ci.yml`               | MySQL (Docker)   | `validate`      | **CI/CD only.** GitHub Actions; MySQL via Docker Compose for production parity. |
| `prod`             | `application-prod.yml`             | Vendor-Specific  | `validate`      | Production; combined with a vendor profile. Secrets via ENV.        |
| `mysql`            | `application-mysql.yml`            | MySQL            | `validate`      | **Vendor profile.** Sets Flyway location for MySQL.                 |
| `postgresql`       | `application-postgresql.yml`       | PostgreSQL       | `validate`      | **Vendor profile.** Sets Flyway location for PostgreSQL.            |
| `security`         | `application-security.yml`         | -                | -               | Activates or overrides security-specific settings.                  |

**Note**: Profiles can be combined. For example, a production environment would use `prod,mysql` or `prod,postgresql`.

---

## Running with Profiles

Profiles can be set with the `SPRING_PROFILES_ACTIVE` environment variable or a JVM argument.

```bash
# Run with a combined dev and mysql profile
SPRING_PROFILES_ACTIVE=dev,mysql ./gradlew bootRun

# Run tests with the CI profile
SPRING_PROFILES_ACTIVE=ci ./gradlew test

# Example for a production Docker container
docker run -d -e SPRING_PROFILES_ACTIVE=prod,postgresql --env-file .env.prod phoebe:latest
```
If no profile is specified, `local` is the default for the main app, and `test` is the default for tests.

## Environment Variables & .env

### Local Development (.env)
`.env` (ignored by git) provides DB and app credentials locally.
`.env.example` is included in the repo with placeholders (changemePass etc).

Example `.env`:
```# MySQL credentials
MYSQL_ROOT_PASSWORD=rootpass
MYSQL_DATABASE=newsdb
MYSQL_USER=newsuser
MYSQL_PASSWORD=newspass

# Ports
SPRING_LOCAL_PORT=8080
DATABASE_LOCAL_PORT=3306

# Spring datasource
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:${DATABASE_LOCAL_PORT}/newsdb?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
SPRING_DATASOURCE_USERNAME=${MYSQL_USER}
SPRING_DATASOURCE_PASSWORD=${MYSQL_PASSWORD}

# Authentication credentials (Basic Auth)
ADMIN_USERNAME=admin
ADMIN_PASSWORD=changemeAdmin
EDITOR_USERNAME=editor
EDITOR_PASSWORD=changemeEditor

# Rate limiting (optional, defaults applied if not set)
# PUBLIC_RATE_LIMIT=100
# ADMIN_RATE_LIMIT=50
```
**Note**: Secrets must never be committed to git. Only `.env.example` should be version-controlled.

## Secrets Management

### Authentication Security
- **Current**: Basic Auth with database-backed user credentials.
- **Role separation**: ADMIN (full access), EDITOR (content management).
- **Planned migration**: OAuth 2.0 + 2FA for ADMIN and EDITOR roles, replacing Basic Auth.
- **BCrypt encoding** for password security (current implementation).

### Environment Management
- **Locally**: `.env` file (git-ignored).
- **CI/CD**: GitHub Actions → Repository Secrets.
- **Production**: Environment variables or runtime-mounted secret files.
- Passwords, tokens, and admin credentials should always be injected via environment variables.
- Never hardcode secrets in `application-*.yml` files or commit real credentials.

## Best Practices
- Keep `application.yml` for minimal, non-sensitive defaults only.
- Use `.env` for local development only; commit `.env.example` as a template.
- Production configurations (the `prod` profile) should read data exclusively from environment variables.
- Align the database schema strategy for each environment: `update` locally, `validate` in production.
