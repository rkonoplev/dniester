# Developer Guide – Local Workflow and CI/CD Expectations

## Table of Contents
- [Local Development Workflow](#local-development-workflow)
- [Before Pushing to GitHub](#before-pushing-to-github)
- [Summary](#summary)
- [Project Setup](#project-setup)
- [Development Environment](#development-environment)
- [Production Environment](#production-environment)
- [Database Migrations (Flyway)](#database-migrations-flyway)
- [Backend Layer Structure](#backend-layer-structure)
- [Code Quality & Security Tools](#code-quality--security-tools)
- [Common Development Commands](#common-development-commands)
- [Daily Workflow](#daily-workflow)
- [MySQL Handy Commands Cheat Sheet](#mysql-handy-commands-cheat-sheet)


This document explains how developers should work with the project locally (IntelliJ IDEA, Gradle, Docker)
and what checks will be automatically run in GitHub Actions (CI/CD).

---

## Local Development Workflow

You do **not** need to keep Docker containers or databases running all the time during active development.  
Focus on the code and use Docker only when you want to test the full application. The heavy checks like
static analysis, security scanning, and code coverage are performed in GitHub Actions.

### Daily workflow in IntelliJ IDEA:
- **Write code** and use `Build Project` (`Ctrl+F9`) to compile changes.
- **Run unit tests** frequently:
    - Right-click test class/method → `Run Test`.
    - Or run via Gradle:
      ```bash
      ./gradlew test
      ```
- **Check style/linting locally** (optional, prevents CI failures):
  ```bash
  ./gradlew checkstyleMain checkstyleTest
  ```

## Before Pushing to GitHub

Before committing and pushing, check at least:

- Code compiles (Build Project or `./gradlew build`)
- All **tests pass** (`./gradlew test`)
- Code style checks pass (optional but strongly recommended)

That’s usually enough — **GitHub Actions CI** will run additional steps:

- Full Gradle build + unit tests.  
- Static analysis with Checkstyle and PMD.  
- JaCoCo coverage report + Codecov upload.  
- GitLeaks secrets scanning.

## Summary

Developers **can work without Docker** most of the time.
Run **unit tests and build locally** before pushing.
Let **CI/CD (GitHub Actions)** handle static analysis, coverage, and security.

This approach ensures fast, resource-light local development, while CI validates everything in the cloud.

---

## Project Setup

For a first-time setup, the project supports two main scenarios:

1.  **Migrating from Drupal 6**: This path uses a MySQL database.
2.  **Starting a clean installation**: You can choose either MySQL or PostgreSQL.

> For detailed, step-by-step instructions for both scenarios, please refer to the
> **[Setup Guide](./SETUP_GUIDE.md)**.


## Development Environment

The project uses Docker Compose to manage the local development environment. The daily start command is:

```bash
docker compose --env-file .env.dev up -d
```

To check the running containers:
```bash
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Ports}}\t{{.Status}}"
```

To stop all services:
```bash
docker compose down
```

## Production Environment

The production environment should be deployed using a CI/CD pipeline. The setup uses the base `docker-compose.yml`
and injects production secrets securely. For more details, see the **[Docker Guide](DOCKER_GUIDE.md)**.

## Database Migrations (Flyway)

The project uses Flyway to manage database schema evolution. To support multiple database systems (MySQL and
PostgreSQL), the migration scripts are organized into vendor-specific directories.

### Directory Structure
- `src/main/resources/db/migration/common/`: Contains scripts compatible with all supported databases.
- `src/main/resources/db/migration/mysql/`: Contains MySQL-specific scripts, used when the `mysql` profile is active.
- `src/main/resources/db/migration/postgresql/`: Contains PostgreSQL-specific scripts, used with the `postgresql` profile.

### How It Works
Flyway's script locations are configured via the active Spring profile in the `application-{profile}.yml` file.

The configuration in `application-mysql.yml` is:
```yaml
spring:
  flyway:
    locations: classpath:db/migration/common,classpath:db/migration/mysql
```

This setup allows Flyway to combine common and database-specific migrations, ensuring the schema is correctly
applied for the target environment.


## Backend Layer Structure

- `controller` — REST API controllers (Public + Admin)
- `service` — Business logic layer
- `repository` — JPA data access layer
- `dto` — Data Transfer Objects
- `mapper` — Entity ↔ DTO mapping
- `entity` — JPA entities
- `config` — Security, rate limiting, test configurations
- `filter` — Rate limiting filter

---

## Code Quality & Security Tools

This project uses several tools for code and security assurance.

- **Checkstyle**: Enforces a consistent coding style. Config: `config/checkstyle/checkstyle.xml`.
- **PMD**: Detects common programming flaws.
- **JaCoCo**: Measures code coverage by unit tests.
- **GitLeaks**: Scans for hardcoded secrets.

## Common Development Commands

| Task                    | Command                                   |
|-------------------------|-------------------------------------------|
| Run all checks          | `./gradlew check`                         |
| Run code style checks   | `./gradlew checkstyleMain checkstyleTest` |
| Run tests with coverage | `./gradlew test`                          |
| Secret scan             | `gitleaks detect --source .`              |

---

## Daily Workflow

**Do not need to import** `clean_schema.sql` every time you restart your computer.

### Why?
- The MySQL container stores all database data inside a persistent Docker volume (`mysql_data`).
- This volume survives container restarts and system reboots.

### Rules
- Use `docker compose up -d` every morning → your data is still there.
- Do NOT run `docker compose down -v` unless you want to wipe all data and re-import.

---

## MySQL Handy Commands Cheat Sheet

### 1. Connect to MySQL container (interactive shell)

```bash
docker exec -it news-mysql mysql -uroot -proot
```

### 2. Dumping databases (export)

```bash
docker exec -i news-mysql mysqldump -uroot -proot dniester > db_data/exported_dump.sql
```

### 3. Importing dumps

```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/exported_dump.sql
```
