# Architecture Migration Plan (Drupal 6 → Spring Boot, MySQL, Docker)

This document outlines the migration process from the legacy Drupal 6.0 PHP platform to a modern **Java 21 / Spring Boot** backend with **MySQL** and **Docker**.  
It summarizes completed phases, next steps, branching strategy, and best practices.

---

## Goals

- **Minimal future intervention** — keep the system simple to maintain.
- **Low maintenance cost** — rely on Spring Boot conventions and Docker.
- **Stable operation** even on free-tier or managed services.
- **Clean, pragmatic project structure** — avoid unnecessary complexity (no heavy DDD).
- **Fast developer onboarding** — easy setup with Docker and `.env` file.

---

## Completed Improvements

1.  **Database Migration to MySQL**
    - Moved from MariaDB → MySQL for more stable support for schema features.
    - JDBC drivers and connection URLs updated accordingly.
    - Docker Compose configured with a dedicated MySQL service.

2.  **Dockerization**
    - Production-ready `Dockerfile` using a slim JRE image.
    - `docker-compose.yml` for orchestrating the application and database services.
    - One-command startup for a complete local environment.

3.  **Profile-based Configuration**
    - All configurations migrated to **YAML** (`application.yml` + `application-<profile>.yml`).
    - Profiles established for `local`, `dev`, `test`, `ci`, and `prod` environments.
    - Secrets and credentials are injected from `.env` files or environment variables, with no hardcoded values in the repository.

4.  **Robust API and Service Layer**
    - **Global Exception Handling**: A `@ControllerAdvice` is implemented for unified JSON error responses.
    - **Input Validation**: DTOs are validated using annotations (`@NotNull`, `@Size`) to ensure data integrity.
    - **API Documentation**: Integrated `springdoc-openapi-ui` for live, interactive Swagger UI documentation.

5.  **Enhanced Security & Performance**
    - **Endpoint Security**: Endpoints are segregated into `/api/public/` and `/api/admin/`, with role-based authorization enforced on the admin endpoints.
    - **CORS Configuration**: Centralized CORS rules in a dedicated configuration for security and flexibility.
    - **Caching**: Implemented high-performance in-memory caching with Caffeine to reduce database load and improve response times for frequently accessed data.

---

## Git Strategy & Rollback

### Archive the legacy branch
```bash
git checkout main
git pull origin main
git branch main-legacy
git push origin main-legacy
```
### Start migration on a new branch
```bash
git checkout -b feature/minimal-improvements
```
### To rollback to legacy
```bash
git checkout main-legacy
git checkout -b main
git push origin main --force
```
## Recommended Order of Implementation
Global error handling & validation.
CORS and security improvements.
(Completed) MySQL migration + Dockerization + Profiles.
Swagger/OpenAPI integration + documentation updates.
## Benefits
Consistency — unified error handling and validation.
Portability — Docker + profile-driven configs = can run anywhere.
Security — strict separation public/admin APIs with role-based auth.
Maintainability — lean, minimal dependencies, easy updates.
Developer Experience — Swagger, clear onboarding, simple Docker setup.
## Notes
All secrets and credentials must come from .env, CI secrets, or Docker/Cloud secret managers.
Never commit DB credentials, admin passwords, or tokens into git.
Database dumps (migrations) must use UTF-8 encoding (prevent broken Cyrillic import/export).
Documentation should evolve as the migration continues.


## Archived Legacy Branch: `main-legacy`

As of August 14, 2025, the `main-legacy` branch has been officially archived and locked for changes.  
It contains deprecated code and is preserved solely for historical reference.  
All active development now takes place in the `main` branch or other current branches.

Final commit before archival: `8978e8845a911aec1f2271e4e17f5013ef700efb`
