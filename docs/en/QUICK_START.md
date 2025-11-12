# Quick Start Guide

> For definitions of key terms and technologies, please refer to the **[Glossary](./GLOSSARY.md)**.

Quick instructions for developers for **daily work** with an already configured project.

> **Important**: For **initial project setup**, please follow the detailed
> [Setup Guide](./SETUP_GUIDE.md).

---

## Requirements
- JDK 21+
- Docker & Docker Compose
- Git

---

## Daily Workflow

### 1. Start Development Environment

This command starts all necessary services (database and application) defined in `docker-compose.yml`.

```bash
docker compose --env-file .env.dev up -d
```

### 2. Check Status

- **Check containers**: `docker ps` → should show `phoebe-app` and `phoebe-mysql`
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **Admin panel**: login `admin`, password `admin`

### 3. Stop Environment

To stop all services and free up resources:
```bash
docker compose down
```

> **Note**: Using `docker compose down -v` will completely remove your database data.
> Use this command only if you want to start with a clean slate.

---

## Main Development Commands

All commands are executed from the `backend/` directory.

### Run Tests
```bash
./gradlew clean test
```

### Build Project
```bash
./gradlew build
```

### Code Quality Check
```bash
./gradlew checkstyleMain checkstyleTest
```

---

## Troubleshooting

### Tests Not Running (MySQL Conflict)
- Check that `application.yml` does **not** have `spring.profiles.active: local`
- In `application-test.yml` Flyway should be disabled
- Run tests with clean build: `./gradlew clean test`

### Reset Database
```bash
docker compose down -v  # removes all data
docker compose up -d    # creates clean database
```

### Port Issues
- MySQL: port 3306 (may conflict with local MySQL)
- Spring Boot: port 8080
- Stop local services or change ports in `.env.dev`

---

## Additional Documentation

- **[Setup Guide](./SETUP_GUIDE.md)**: Step-by-step instructions for first-time setup.
- **[Developer Guide](./DEVELOPER_GUIDE.md)**: Detailed IDE setup and workflow description.
- **[Project Overview](./PROJECT_OVERVIEW.md)**: Complete information about architecture and technologies.
- **[Docker Guide](./DOCKER_GUIDE.md)**: Advanced container operations.
- **[Drupal 6 Migration](./MIGRATION_DRUPAL6.md)**: Migration process.

---

## CI/CD

- **GitHub Actions**: automatically runs `ci` profile with H2.
- **Tests**: executed on every push and PR.
- **Code Quality**: Checkstyle, JaCoCo coverage.

---

## Production

- Uses `docker-compose.override.yml` with secrets.
- `prod` profile with real MySQL.
- Environment variables via Docker secrets.