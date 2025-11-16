> [Back to Documentation Contents](./README.md)

# Docker Guide

> For a definition of key terms and technologies, please refer to the **[Glossary](./GLOSSARY.md)**.

## Table of Contents
- [Docker Compose Strategy](#docker-compose-strategy)
- [Practical Scenarios and Commands](#practical-scenarios-and-commands)
- [Production Build with Docker](#production-build-with-docker)
- [Best Practices](#best-practices)

This document explains how to work with Docker in Phoebe CMS, both for local development
and production deployment.

---

## Docker Compose Strategy

The project uses a two-file approach for Docker Compose to separate the core application architecture
from local development conveniences.

### `docker-compose.yml` (The Architectural Blueprint)
-   **Purpose**: Defines the fundamental services that make up the application (`backend`, `database`, `frontend`).
-   **Analogy**: Think of this file as the architectural plan for a building. It describes the foundation,
    the number of floors, and the main structure. It is essential and universal for any environment.

### `docker-compose.override.yml` (The Developer's Scaffolding)
-   **Purpose**: Provides local, development-specific overrides. This file is automatically and transparently
    used by Docker Compose when you run `docker-compose up`.
-   **Analogy**: This is the temporary scaffolding used to construct the building. It's essential for the
    development process but is not part of the final structure.
-   **Content**: It typically includes configurations that are only useful for local development, such as:
    -   **Port Mapping**: Exposing the database port to the host machine for direct access (`3306:3306`).
    -   **Volume Mounts**: Mounting local source code into the container for "hot-reloading".

This separation ensures that the base `docker-compose.yml` remains clean, while developers have the
flexibility to customize their local environment.

To start the backend and database together for local development, run:
```bash
docker-compose up --build
```
*   **Note**: The recommended way to start the local development environment is to use the `make run` command, which automates this process.

---

## Practical Scenarios and Commands

While `make` is the primary interface, understanding the underlying `docker compose` commands provides more flexibility.

### Recommendations for Common Tasks

| Goal | Commands |
|:---|:---|
| 🔄 **Quickly restart with updated code** | `docker compose up --build phoebe-app` |
| 🧹 **Completely clean and recreate** | `docker compose down -v`<br>`docker compose build --no-cache phoebe-app`<br>`docker compose up` |
| 🧪 **Test only one service** | `docker compose up phoebe-app` |
| 🧱 **Full rebuild of all services** | `docker compose build --no-cache`<br>`docker compose up` |

### `docker compose` vs. `make` Commands

| `docker compose` Command | What it does | `make` Equivalent | Notes |
|:---|:---|:---|:---|
| `docker compose up` | Starts containers. Builds if images are missing (with cache). | `make run` (old version) | A fast command for normal development. |
| `docker compose up --build` | Always rebuilds images before starting, but uses cache. | `make run` | Ensures code changes are applied. |
| `docker compose up --build phoebe-app` | Rebuilds only `phoebe-app` (with cache) and starts services. | - | Faster if you only need to update the backend. |
| `docker compose build --no-cache phoebe-app` | Rebuilds the `phoebe-app` image from scratch, ignoring cache. | `make rebuild` | Useful for changes in `Dockerfile` or `build.gradle`. |
| `docker compose down` | Stops and removes containers, but keeps volumes. | `make stop` | **Safe**: MySQL data is preserved. |
| `docker compose down -v` | Stops everything and removes volumes. | `make reset` | **Dangerous**: Resets the database. Use for a "clean slate." |

### What's the difference: `--build` vs. `--no-cache`?

- **`--build`** = Update with cache (🏎️ **fast**).
  - Docker rebuilds only the image layers that have changed. Ideal for daily development.
  - Command: `docker compose up --build`

- **`--no-cache`** = Rebuild from scratch (🧹 **clean, but slow**).
  - Docker ignores all cache and builds the image from the very beginning.
  - Use this when changing the `Dockerfile`, dependencies in `build.gradle`, or when the cache causes issues.
  - Command: `docker compose build --no-cache phoebe-app`

---

## Production Build with Docker

1.  **Build application JAR**:
    ```bash
    cd backend
    ./gradlew bootJar
    ```

2.  **Build production Docker image**:
    ```bash
    docker build -t phoebe-cms:1.0.0 -f Dockerfile .
    ```
    *   **Note**: It is recommended to use specific versions (e.g., `phoebe-cms:1.0.0`) instead of the `:latest` tag.

3.  **Run production container**:
    ```bash
    docker run -d -p 8080:8080 --env-file .env.prod phoebe-cms:1.0.0
    ```
    *   **Note**: For production, it is recommended to use secret managers (e.g., Kubernetes Secrets, Vault) instead of `--env-file`.

   - Based on a lightweight JRE runtime image.
   - Contains only the compiled JAR (no source code).
   - Configuration comes from environment variables.

---

## Best Practices
- Keep the production Dockerfile minimal (no Gradle, only the JAR).
- Use `Dockerfile.dev` for developer productivity (mounted source, `bootRun`).
- **Store database dumps in the `./db_dumps` directory** for easy import/export.
- Manage all secrets via `.env` (local) and a secret manager (CI/CD/Prod).
- Ensure secrets and `.env` files are excluded from git.
