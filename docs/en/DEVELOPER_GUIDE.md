# Developer Guide – Local Workflow and CI/CD Expectations

## Table of Contents
- [Local Development Workflow](#local-development-workflow)
- [Before Pushing to GitHub](#before-pushing-to-github)
- [Summary](#summary)
- [Development Environment](#development-environment)
- [Production Environment](#production-environment)
- [Migration from Drupal 6](#migration-from-drupal-6)
- [Backend Layer Structure](#backend-layer-structure)
- [Code Quality & Security Tools](#code-quality--security-tools)
    - [Local Analysis](#local-analysis)
    - [Cloud Analysis](#cloud-analysis)
    - [Security](#security)
- [Common Development Commands](#common-development-commands)
- [Running the Project](#running-the-project)
    - [Option A. Run only the database (MySQL check)](#option-a-run-only-the-database-mysql-check)
    - [Option B. Run the full stack (Spring Boot + MySQL)](#option-b-run-the-full-stack-spring-boot--mysql)
    - [Quick TL;DR](#quick-tldr)
- [Daily Workflow](#daily-workflow)
- [MySQL Handy Commands Cheat Sheet](#mysql-handy-commands-cheat-sheet)
- [Database Schema](#database-schema)


This document explains how developers should work with the project locally (IntelliJ IDEA, Gradle, Docker),
and what checks will be automatically run in GitHub Actions (CI/CD).

---

## Local Development Workflow

You do **not** need to keep Docker containers or databases running all the time during active development.  
Focus on the code and use Docker only when you want to test the full application. The heavy checks
(static analysis, security scanning, code coverage, etc.) are performed in GitHub Actions.

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
 ### Optional (when needed):
  If you want to **run the full Spring Boot application locally:**
  Start Docker (e.g., database containers).
  Run the service with:
  ```bash
  ./gradlew bootRun
  ```
  After testing, stop Docker to avoid unnecessary CPU/memory usage.
 
 ## Before Pushing to GitHub
  Before committing and pushing, check at least:

- Code compiles (Build Project or ./gradlew build)
- All **tests pass** (./gradlew test)
- Code style checks pass (./gradlew checkstyleMain checkstyleTest)
(optional but strongly recommended)

That’s usually enough — **GitHub Actions CI** will run additional steps:

- Full Gradle build + unit tests.  
- Static analysis with Checkstyle and PMD.  
- JaCoCo coverage report + Codecov upload.  
- GitLeaks secrets scanning.  
- Code scanning alerts integration in GitHub Security.

## Summary
Developers **can work without Docker** most of the time.
Run **unit tests and build locally** before pushing.
Let **CI/CD (GitHub Actions)** handle static analysis (Checkstyle + PMD), coverage (JaCoCo + Codecov),
and security (GitLeaks).

This approach ensures fast, resource-light local development, while CI validates everything in the cloud.

**Note**: Future authentication migration to OAuth 2.0 + 2FA is planned for the ADMIN and EDITOR roles.

## Development Environment

The project uses Docker Compose to manage the local development environment, which consists of the backend application
and a MySQL database.

To start the local environment, run:
```bash
docker compose --env-file .env.dev up -d
```
This command automatically uses both `docker-compose.yml` (the base configuration) and `docker-compose.override.yml`
(for local development tweaks). For a detailed explanation of how these two files work together, please see the
**[Docker Guide](DOCKER_GUIDE.md#understanding-docker-composeyml-vs-docker-composeoverrideyml)**.

To check the running containers:
```bash
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Ports}}\t{{.Status}}"
```
To connect to the database:
```bash
docker exec -it news-mysql mysql -uroot -proot dniester
```
To stop all services:
```bash
docker compose down
```

## Production Environment

The production environment should be deployed using a CI/CD pipeline. The setup uses the base `docker-compose.yml`
and injects production secrets securely. The `docker-compose.override.yml` file is **not** used in production.

A simplified command for a production-like start would be:
```bash
docker compose -f docker-compose.yml --env-file .env.prod up -d
```
Note: `.env.prod` must NOT be committed. In a real production scenario, these variables would be provided by the
hosting platform (e.g., Render, AWS) or a secrets manager. For more details on production builds, see the
**[Docker Guide](DOCKER_GUIDE.md)**.

## Migration from Drupal 6
For complete migration instructions, see [Migration Drupal6 → Phoebe CMS](MIGRATION_DRUPAL6.md).


## Backend Layer Structure

- `controller` — REST API controllers (Public + Admin)
- `service` — Business logic layer
- `repository` — JPA data access layer with term-based queries
- `dto` — Data Transfer Objects
- `mapper` — Entity ↔ DTO mapping
- `entity` — JPA entities (News, User, Term, Role)
- `config` — Security, rate limiting, test configurations
- `filter` — Rate limiting filter with Bucket4j

---

## Code Quality & Security Tools

This project uses several tools for code and security assurance.

### Local Analysis

- **Checkstyle**
    - Config: `config/checkstyle/checkstyle.xml`
    - Run:
      ```bash
      ./gradlew checkstyleMain checkstyleTest
      ```

- **JaCoCo**
    - Run:
      ```bash
      ./gradlew test
      ```  
    - Report: `build/reports/jacoco/test/html/index.html`

### Cloud Analysis

- **Checkstyle** — style guide and formatting enforcement, runs in CI
- **PMD** — detects code smells and common programming issues, runs in CI
- **CodeCov** — publishes code coverage reports to GitHub PRs (via JaCoCo)

### Security

- **GitLeaks** (secret scanning tool)  
  Run:
  ```bash
  gitleaks detect --source .

---

## Common Development Commands

| Task                    | Command                                   |
|-------------------------|-------------------------------------------|
| Run all checks          | `./gradlew check`                         |
| Run code style checks   | `./gradlew checkstyleMain checkstyleTest` |
| Run tests with coverage | `./gradlew test`                          |
| Secret scan             | `gitleaks detect --source .`              |
| Test public API         | `curl -i "http://localhost:8080/api/public/news"` |
| Test admin API          | `curl -u admin:password -i "http://localhost:8080/api/admin/news"` |
| Test rate limiting      | `for i in {1..105}; do curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/public/news; done` |

---
## Running the Project

After migrating the database and producing `clean_schema.sql`, you can run the full Phoebe CMS stack
(MySQL + Spring Boot) or only the database for verification.

### Option A. Run only the database (MySQL check)

```bash
# Stop any previous containers and volumes
docker compose -f docker-compose.yml down -v

# Start MySQL 8.0 only
docker compose -f docker-compose.yml up -d mysql

# Check logs
docker logs -f news-mysql

# Import the clean schema (if not imported yet)
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql

# Verify content
docker exec -it news-mysql mysql -uroot -proot -e "USE dniester; SHOW TABLES;"
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```
### Option B. Run the full stack (Spring Boot + MySQL)
**1. Ensure you have .env.dev in the project root with proper DB configs:**

```dotenv
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=dniester
SPRING_LOCAL_PORT=8080
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/dniester?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
```
**2. Start containers:**

```bash
docker compose --env-file .env.dev up -d
```
news-mysql → MySQL 8.0 with schema dniester
news-app → Spring Boot backend

**3. Import clean schema if needed:**

```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
```
**4. Verify DB data:**

```bash
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```
**5. Check application logs:**

```bash
docker logs -f news-app
```
**6. Test API endpoints:**

```bash
# Public API (no auth, rate limited 100/min)
curl -i "http://localhost:8080/api/public/news?size=5"

# Admin API (basic auth, rate limited 50/min)
curl -u admin:password -i "http://localhost:8080/api/admin/news"

# Swagger UI
http://localhost:8080/swagger-ui/index.html
```
### Quick TL;DR
```bash
# Start full stack
docker compose --env-file .env.dev up -d

# Import schema (if needed)
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql

# Verify data
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester

# Test APIs
curl -i "http://localhost:8080/api/public/news?size=3"
curl -u admin:password -i "http://localhost:8080/api/admin/news"
```
---

## Starting Scenarios

Phoebe CMS supports two distinct starting scenarios for administrators and developers.

### Scenario 1: Migrate from Drupal 6 (Legacy Data)

This is the primary scenario for projects with an existing Drupal 6 installation.

1.  Follow the complete migration process outlined in **[Migration Drupal6 → Phoebe CMS](MIGRATION_DRUPAL6.md)**.
2.  This process produces the `db_data/clean_schema.sql` file, which contains all your legacy news, users, and taxonomy.
3.  Start the application stack:
    ```bash
    docker compose --env-file .env.dev up -d
    ```
4.  **No additional setup is needed.** Your migrated admin users (from Drupal) can log in with their original credentials.

### Scenario 2: Start from Scratch (Clean Database)

This scenario is ideal for new projects or development environments.

1.  Ensure you have a clean database. If you have existing data, wipe it:
    ```bash
    docker compose down -v
    ```
2.  Start the database and application:
    ```bash
    docker compose --env-file .env.dev up -d
    ```
    Spring Boot will automatically create the required tables (`users`, `roles`, `content`, etc.) using Hibernate DDL.
3.  **Create a default admin user** by running the initialization script:
    ```bash
    docker exec -i news-mysql mysql -uroot -proot dniester < db_data/create_admin_user.sql
    ```
    > **Important**: Default password is `admin` (BCrypt hash already included in script).
4.  You can now log in to the admin panel with the credentials you configured in the script (default: `admin` / `admin`).

## Daily Workflow

**Do not need to import** `clean_schema.sql` every time you restart your computer.

### Why?
- MySQL container stores all database data inside `/var/lib/mysql`.
- In `docker-compose.yml` we mounted a persistent Docker volume:
  ```yaml
  volumes:
    - mysql_data:/var/lib/mysql
  ```
This volume (news-platform_mysql_data) survives container restarts and system reboots.

### Rules
- Use docker compose up -d every morning → your data is still there.
- Do NOT run docker compose down -v unless you want to wipe all data and re-import.

### What should be running?
**Option A (DB only):**

- Containers: news-mysql
- Volumes: news-platform_mysql_data

**Option B (DB + backend):**

- Containers: news-mysql + news-app
- Volumes: news-platform_mysql_data

The old migration container news-mysql-drupal6 and its volume news-platform_mysql_data_drupal6 can be safely removed.

**Daily Start Command**
```bash
docker compose --env-file .env.dev up -d
```
Check:

```bash
docker ps
docker volume ls | grep news-platform
```
Expected:

- Containers: news-mysql (+ news-app)
- Volumes: news-platform_mysql_data

---

## MySQL Handy Commands Cheat Sheet

### 1. Connect to MySQL container (interactive shell)

```bash
docker exec -it news-mysql mysql -uroot -proot
```
Now you are inside the MySQL CLI (mysql>):

List all databases:

```sql
SHOW DATABASES;
```
Select the active database:

```sql
USE dniester;
```
List all tables:

```sql
SHOW TABLES;
```
Example: check content count:

```sql
SELECT COUNT(*) FROM content;
```
Exit MySQL CLI:

```sql
EXIT;
```
**2. Dumping databases (export)**
From the terminal (outside MySQL):

Dump a single database:

```bash
docker exec -i news-mysql mysqldump -uroot -proot dniester > db_data/exported_dump.sql
```
Dump a specific table:

```bash
docker exec -i news-mysql mysqldump -uroot -proot dniester users > db_data/users_dump.sql
```
**3. Importing dumps**
Load an SQL file back into MySQL:

```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/exported_dump.sql
```
Notes:

Replace dniester with the db name you want to use.
Make sure the database exists before importing.

## Database Schema

The full MySQL 8 database schema (DDL) is documented in [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md).  
It includes `users`, `roles`, `user_roles`, `content`, `terms`, and `content_terms` tables. 