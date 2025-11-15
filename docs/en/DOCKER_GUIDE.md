# Docker Guide

## Table of Contents
- [Local Development with Docker Compose](#local-development-with-docker-compose)
  - [Understanding `docker-compose.yml` vs. `docker-compose.override.yml`](#understanding-docker-composeyml-vs-docker-composeoverrideyml)
- [Production Build with Docker](#production-build-with-docker)
- [Best Practices](#best-practices)


This document explains how to work with Docker in Phoebe CMS, both for local development
and production deployment.

---

## Local Development with Docker Compose

The project uses a two-file approach for Docker Compose to separate the core application architecture
from local development conveniences.

### Understanding `docker-compose.yml` vs. `docker-compose.override.yml`

#### `docker-compose.yml` (The Architectural Blueprint)
-   **Purpose**: Defines the fundamental services that make up the application. It states that the platform
    consists of a `backend` service and a `database` service.
-   **Analogy**: Think of this file as the architectural plan for a building. It describes the foundation,
    the number of floors, and the main structure. It is essential and universal for any environment.
-   **Content**: Contains service definitions, network configurations, and volume stubs that are common
    across all environments.

#### `docker-compose.override.yml` (The Developer's Scaffolding)
-   **Purpose**: Provides local, development-specific overrides. This file is automatically and transparently
    used by Docker Compose when you run `docker-compose up`.
-   **Analogy**: This is the temporary scaffolding used to construct the building. It's essential for the
    development process but is not part of the final structure.
-   **Content**: It typically includes configurations that are only useful for local development, such as:
    -   **Port Mapping**: Exposing the database port to the host machine for direct access with a GUI client (e.g., `3306:3306` for MySQL).
    -   **Volume Mounts**: Mounting local source code into the container. This allows the container to see file changes, but for application hot-reloading, additional tools (e.g., `spring-boot-devtools`) are required.

This separation ensures that the base `docker-compose.yml` remains clean and representative of the production
architecture, while developers have the flexibility to customize their local environment without affecting the
core configuration.

To start the backend and database together for local development, run:

```bash
docker-compose up --build
```
*   **Note**: The recommended way to start the local development environment is to use the `make run` command, which automates this process.

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
    *   **Note**: It is recommended to use specific versions (e.g., `phoebe-cms:1.0.0`) instead of the `:latest` tag for stability and reproducibility in production.

3.  **Run production container**:
    ```bash
    docker run -d -p 8080:8080 --env-file .env.prod phoebe-cms:1.0.0
    ```
    *   **Note**: For production deployments, it is recommended to use secret managers (e.g., Kubernetes Secrets, HashiCorp Vault, AWS Secrets Manager) instead of `--env-file` for more secure management of sensitive data. Using `--env-file` is suitable for simple deployments or testing.

   - Based on a lightweight JRE runtime image.
   - Contains only the compiled JAR (no source code).
   - Configuration comes from environment variables.

---

## Best Practices
- Keep Dockerfile for production minimal (no Gradle, only JAR).
- Use `Dockerfile.dev` for developer productivity (mounted source, `bootRun`).
- **Store database dumps in the `./db_dumps` directory** for easy import/export.
- Manage all secrets via `.env` (local) and a secret manager (CI/CD/Prod).
- Make sure secrets and `.env` files are excluded from git.
