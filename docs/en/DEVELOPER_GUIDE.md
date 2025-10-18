# Developer Guide – Local Workflow and CI/CD Expectations

## Table of Contents
- [Local Development Workflow](#local-development-workflow)
- [Setting Up Code Autoformatter](#setting-up-code-autoformatter)
- [Before Pushing to GitHub](#before-pushing-to-github)
- [What CI/CD Does (on GitHub Actions)](#what-cicd-does-on-github-actions)
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

---

## Setting Up Code Autoformatter

The project enforces a consistent Java code formatting style with a line length of **120 characters**.
The configuration is located in `.idea/codeStyles/` and is automatically applied when you open the project in IntelliJ IDEA.

#### Automatic Setup
- Formatting settings are automatically picked up by IntelliJ IDEA.
- Verify this by navigating to: `File → Settings → Editor → Code Style → Scheme = "Project"`.

#### Usage
- **Format Code**: Use `Ctrl+Alt+L` (Windows/Linux) or `Cmd+Alt+L` (Mac) to reformat the current file or selected code.
- **Format on Save**: Enable this feature for automatic formatting when saving files:
  `Settings → Tools → Actions on Save → Reformat code`.

#### Key Rules
- **Line Length**: 120 characters.
- **Wrap on Typing**: Long method chains and parameters are automatically wrapped.
- **Consistent Style**: Uniform bracket and indentation style.
- **Auto-formatting**: Imports and empty lines are automatically managed.

All developers on the team use these identical formatting settings to ensure code consistency.

---

## Before Pushing to GitHub

Before committing and pushing, check at least:

- Code compiles (Build Project or `./gradlew build`)
- All **tests pass** (`./gradlew test`)
- Code style checks pass (optional but strongly recommended)

---

## What CI/CD Does (on GitHub Actions)

After pushing your changes, the following automated checks will be performed by GitHub Actions:
- Full Gradle build + unit tests.
- Static code analysis: Checkstyle and PMD.
- Test coverage report (JaCoCo) + upload to Codecov.
- GitLeaks secret scanning.
- Integration with GitHub Security (Code scanning alerts).

---

## Summary

Developers **can work without Docker** most of the time.
Run **unit tests and build locally** before pushing.
Let **CI/CD (GitHub Actions)** handle static analysis, coverage, and security.

This approach ensures fast, resource-light local development, while CI validates everything in the cloud.

> **Note:** Authentication migration to OAuth 2.0 + 2FA for all roles (ADMIN, EDITOR) is planned.

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

### Database Setup and Application Startup

For local development, you'll primarily interact with your chosen database via Docker.

1.  **Start Database Containers**: 
    To start your database (MySQL or PostgreSQL, depending on your `docker-compose.yml` configuration and `.env.dev` settings), use:
    ```bash
    # To start all services defined in docker-compose.yml (including database)
    docker compose --env-file .env.dev up -d
    ```
    If you only want to start a specific database service (e.g., MySQL):
    ```bash
    docker compose --env-file .env.dev up -d mysql
    # Or for PostgreSQL:
    # docker compose --env-file .env.dev up -d postgres
    ```

2.  **Run the Application**:
    Once your database container is running, you can start the Spring Boot application. Ensure you activate the correct Spring profile corresponding to your chosen database.
    ```bash
    cd backend

    # For MySQL:
    ./gradlew bootRun --args='--spring.profiles.active=local,mysql'

    # For PostgreSQL:
    # ./gradlew bootRun --args='--spring.profiles.active=local,postgresql'
    ```
    On the first run, Flyway will automatically create the entire table structure in your database based on the active profile.

3.  **Stopping Docker Services**:
    To stop all services and free up resources:
    ```bash
    docker compose down
    ```
    **Important**: Using `docker compose down -v` will completely remove your database data volumes. Only use this command if you intend to wipe all data and start with a fresh database. Otherwise, your data will persist across `docker compose up` and `down` cycles.

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

- `controller` — REST API controllers (Public + Admin): Handles incoming HTTP requests and returns responses.
- `service` — Business logic layer: Contains the core application logic and orchestrates operations.
- `repository` — JPA data access layer: Manages interactions with the database.
- `dto` — Data Transfer Objects: Objects used to transfer data between layers, often for API requests/responses.
- `mapper` — Entity ↔ DTO mapping: Converts between JPA entities and DTOs.
- `entity` — JPA entities: Represents tables in the database.
- `config` — Security, rate limiting, test configurations: Defines application-wide settings.
- `filter` — Rate limiting filter: Intercepts requests to enforce rate limits.

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
