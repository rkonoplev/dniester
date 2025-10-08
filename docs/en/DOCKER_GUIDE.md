# Docker Guide

## Table of Contents
- [Local Development with Docker Compose](#local-development-with-docker-compose)
  - [Understanding `docker-compose.yml` vs. `docker-compose.override.yml`](#understanding-docker-composeyml-vs-docker-composeoverrideyml)
- [Production Build with Docker](#production-build-with-docker)
    - [1. Build application JAR](#1-build-application-jar)
    - [2. Build production Docker image](#2-build-production-docker-image)
    - [3. Run production container](#3-run-production-container)
    - [Secrets Management in Docker](#secrets-management-in-docker)
- [Local vs Production – Summary Table](#local-vs-production--summary-table)
- [Best Practices](#best-practices)


This document explains how to work with Docker in Phoebe CMS, both for local development
and production deployment.

---

## Local Development with Docker Compose

The project uses a two-file approach for Docker Compose to separate the core application architecture
from local development conveniences.

### Understanding `docker-compose.yml` vs. `docker-compose.override.yml`

#### `docker-compose.yml` (The Architectural Blueprint)
-   **Purpose**: Defines the fundamental services that make up the application. It states that the News Platform
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
    -   **Port Mapping**: Exposing the database port to the host machine for direct access with a GUI client.
    -   **Volume Mounts**: Mounting local source code into the container to enable hot-reloading.
    -   **Development-only tools**: Potentially adding extra services like a database admin tool.

This separation ensures that the base `docker-compose.yml` remains clean and representative of the production
architecture, while developers have the flexibility to customize their local environment without affecting the
core configuration.

To start the backend and database together for local development, run:

```bash
docker-compose up --build
```
- **App container (news-app)**
  - Based on Dockerfile.dev
  - Source code is mounted (-v .:/app), so changes in IDE take effect after recompilation
  - Runs Spring Boot with local profile
  - Includes rate limiting (100 req/min public, 50 req/min admin)

- **Database container (news-mysql)**
  - MySQL 8
  - Credentials & schema name from .env
  - Can preload Drupal dump from ./db-dumps/

- After startup, the backend will be accessible at:
http://localhost:8080

## Production Build with Docker
### 1. Build application JAR
   ```bash
   cd backend
   ./gradlew bootJar
   ```
### 2. Build production Docker image
```bash
docker build -t news-platform:latest -f Dockerfile .
```
### 3. Run production container
   ```bash
   docker run -d -p 8080:8080 --env-file .env.dev news-platform:latest
   ```

   - Based on a lightweight JRE runtime image
   - Contains only the compiled JAR (no source code, no Gradle wrapper)
   - Configuration comes from environment variables or injected Secret Files
   
### Secrets Management in Docker
   - Local .env
   Used for local development
   Ignored by git (.gitignore)
   Example: DB username/password, Spring datasource URL
   
   - Docker Secrets in Production
   All sensitive data (DB user/pass, admin credentials) should be passed via Docker Secrets or Render Secrets
   docker-compose.override.yml uses _FILE pattern to read secrets injected into /run/secrets/...
   
   Example preparation of Secrets:
   ```bash
   mkdir -p secrets
   echo "superRootPass" > secrets/mysql_root_password.txt
   echo "newsuser" > secrets/db_user.txt
   echo "secureDbPass" > secrets/db_password.txt
   echo "admin" > secrets/admin_user.txt
   echo "UltraSecure!" > secrets/admin_password.txt
   ```

   Note: Add secrets/ folder to .gitignore to ensure secrets are not committed.

# Local vs Production – Summary Table

| Aspect                | Local Development (`local`)       | Production Deployment (`prod`)             |
|------------------------|-----------------------------------|--------------------------------------------|
| **Config source**      | `.env` + `application-local.yml` | ENV vars + `application-prod.yml` (secrets injected) |
| **Build**              | `Dockerfile.dev` + mounted code  | Slim JRE image + bootJar                   |
| **Secrets**            | Stored in local `.env` (ignored) | Docker Secrets / Render Secrets            |
| **DB access**          | MySQL exposed on `localhost:3306`| Internal network only, not exposed         |
| **Schema strategy**    | `ddl-auto=update`                | `ddl-auto=none` (manual migrations only)   |
| **Rate limiting**      | 100/50 req/min (dev testing)    | 100/50 req/min (production protection)     |
| **Logs**               | Verbose (for developers)         | Minimal (INFO/ERROR only)                  |

## Best Practices
- Keep Dockerfile for production minimal (no Gradle, only JAR).
- Use Dockerfile.dev for developer productivity (mounted source, bootRun from Gradle).
- Always load database dumps into Docker MySQL via ./db-dumps.
- Handle all secrets via .env (local) and Secrets Manager (CI/CD/Prod).
- Make sure secrets and .env files are excluded from git.
