# 🏗️ Architecture Migration Plan (Drupal 6 → Spring Boot, MySQL, Docker)

This document outlines the migration process from the legacy Drupal 6.0 PHP platform to a modern **Java 21 / Spring Boot** backend with **MySQL** and **Docker**.  
It summarizes completed phases, next steps, branching strategy, and best practices.

---

## 🎯 Goals

- **Minimal future intervention** — keep the system simple to maintain.
- **Low maintenance cost** — rely on Spring Boot conventions and Docker.
- **Stable operation** even on free-tier or managed services.
- **Clean, pragmatic project structure** — avoid unnecessary complexity (no heavy DDD).
- **Fast developer onboarding** — easy setup with Docker and `.env` file.

---

## ✅ Completed Improvements

1. **Database Migration to MySQL**
    - Moved from MariaDB → MySQL (more stable support for schema features).

    - JDBC drivers and connection URLs updated accordingly.
    - Docker Compose configured with MySQL service.

2. **Dockerization**
    - Working `Dockerfile` for production (slim JRE image, only JAR inside).
    - `Dockerfile.dev` for development (mounts local project, hot reload).
    - `docker-compose.yml` and `docker-compose.override.yml` for app + DB orchestration.
    - One-command startup:
      ```bash
      docker compose up -d
      ```

3. **Profile-based Configuration**
    - All configs moved to **YAML** (`application.yml` + `application-<profile>.yml`).
    - Profiles: `local`, `dev`, `test`, `ci`, `prod`.
    - Secrets and credentials are injected only from `.env` or CI/CD environment variables (no hardcoded passwords).

---

## 🚧 Next Steps

1. **Global Exception Handling**
    - Introduce `@ControllerAdvice` for unified JSON error responses.
    - Example error format:
      ```json
      {
        "timestamp": "2025-08-14T12:00:00Z",
        "status": 400,
        "message": "Validation failed",
        "details": "/api/admin/news"
      }
      ```

2. **Input Validation**
    - Add annotations (`@NotNull`, `@Size`, etc.) in DTOs (`NewsCreateRequest`, `NewsUpdateRequest`).
    - Use `@Validated` on controller classes.
    - Handle `MethodArgumentNotValidException` via global error handler.

3. **CORS & Security**
    - Move CORS rules into a dedicated `CorsConfig`.
    - Split endpoints:
        - `/api/public/...` → open or limited auth.
        - `/api/admin/...` → secured with JWT + role-based auth.
    - Provide flexible CORS config for frontend teams.

4. **API Documentation**
    - Integrate [springdoc-openapi-ui](https://springdoc.org/) for Swagger UI.
    - Add Swagger link and usage examples to project `README.md`.
    - Document dev/QA workflow for testing endpoints.

---

## 🌳 Git Strategy & Rollback

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
## 📝 Recommended Order of Implementation
Global error handling & validation.
CORS and security improvements.
(Completed) MySQL migration + Dockerization + Profiles.
Swagger/OpenAPI integration + documentation updates.
## 💡 Benefits
Consistency — unified error handling and validation.
Portability — Docker + profile-driven configs = can run anywhere.
Security — strict separation public/admin APIs with JWT & role-based auth.
Maintainability — lean, minimal dependencies, easy updates.
Developer Experience — Swagger, clear onboarding, simple Docker setup.
## 📌 Notes
All secrets and credentials must come from .env, CI secrets, or Docker/Cloud secret managers.
Never commit DB credentials, admin passwords, or tokens into git.
Database dumps (migrations) must use UTF-8 encoding (prevent broken Cyrillic import/export).
Documentation should evolve as the migration continues.


## Archived Legacy Branch: `main-legacy`

As of August 14, 2025, the `main-legacy` branch has been officially archived and locked for changes.  
It contains deprecated code and is preserved solely for historical reference.  
All active development now takes place in the `main` branch or other current branches.

🔗 Final commit before archival: [`8978e88`](https://github.com/rkonoplev/news-platform/commit/8978e8845a911aec1f2271e4e17f5013ef700efb)
