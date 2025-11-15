> For definitions of key terms and technologies, please refer to the **[Glossary](./GLOSSARY.md)**.

Quick instructions for developers for **daily work** with an already configured project.

> **Important**: For **initial project setup**, please follow the detailed
> [Setup Guide](./SETUP_GUIDE.md).

---

## Recommended Quick Start (via Makefile)

The entire primary workflow is built around the `Makefile` for simplicity. This is the easiest way to get started.

- **Run the project**: `make run`
- **Run all tests**: `make all-tests`
- **Stop the project**: `make stop`

> For a complete list of commands and descriptions of their functions, please refer to **[Testing and Development with Makefile](./TESTING_WITH_MAKEFILE.md)**.

---

## Requirements
- JDK 21+
- Docker & Docker Compose
- Git

---

## Alternative Launch (directly via Docker and Gradle)

### 1. Start Development Environment

This command starts all necessary services (database and application) defined in `docker-compose.yml`.

```bash
docker compose up -d
```

### 2. Check Status

- **Check containers**: `docker ps` → should show `phoebe-app` and `phoebe-mysql`
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

### 3. Stop Environment

To stop all services and free up resources:
```bash
docker compose down
```

> **Note**: Using `docker compose down -v` will completely remove your database data.

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

---

## Troubleshooting

### Resetting the Database
```bash
make clean-db
```
Or manually: `docker compose down -v && docker compose up -d`

### Port Conflicts
- **MySQL**: port `3306`
- **Spring Boot**: port `8080`
If these ports are in use on your machine, stop the conflicting services or change the ports in the `.env` file.
