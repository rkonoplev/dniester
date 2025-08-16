# Architecture Migration Plan (MySQL, Spring Boot, Docker)

---

## 🎯 Goals

- **Minimal future intervention** needed
- **Low maintenance cost** for the project
- **Stable operation** on free-tier or managed services
- **Clean, pragmatic project structure** (no overengineering, no DDD)
- **Fast developer onboarding & deployment**

---

## ✅ Completed Improvements

1. **Switch to MySQL**
    - All project data and configuration migrated from MariaDB to MySQL
    - Full Unicode (utf8mb4) support for Drupal 6 → Java migration (handles Cyrillic)
    - Database drivers, connection URLs, and Docker Compose updated for MySQL
2. **Dockerization**
    - Working `Dockerfile` for Spring Boot application
    - Production-grade `docker-compose.yml` — Spring Boot app + MySQL db
    - One-command startup:
      ```bash
      docker compose up -d
      ```
3. **Profile-based Configuration**
    - Environment-specific property files (`application-local.properties`, `application-prod.properties` or YAML alternatives)
    - All secrets and credentials managed via `.env` and environment variables

---

## 🚧 Next Steps

1. **Global Exception Handling**
    - Use `@ControllerAdvice` for unified JSON error responses
    - Example standard error response:
      ```json
      {
        "timestamp": "2025-08-14T12:00:00Z",
        "status": 400,
        "message": "Validation failed",
        "details": "/api/admin/news"
      }
      ```
2. **Input Validation**
    - Add validation annotations (`@NotNull`, `@Size`, etc.) inside DTOs (`NewsCreateRequest`, `NewsUpdateRequest`, etc.)
    - Use `@Validated` on controller classes
    - Handle `MethodArgumentNotValidException` using the global exception handler
3. **CORS and Security**
    - Move CORS rules to a dedicated `CorsConfig` class
    - Fine-tune security:
        - Open or limited-auth public endpoints (`/api/public/...`)
        - Strong protection for admin endpoints (`/api/admin/...`), roles/JWT as needed
    - Flexible CORS config for frontend teams
4. **API Documentation & README**
    - Integrate [springdoc-openapi-ui](https://springdoc.org/) for auto-generated Swagger UI
    - Add Swagger link and sample API usage to `README.md`
    - Document endpoint testing workflow for devs and QA

---

## 🌳 Git Branching and Rollback

**Archive current main branch:**
```bash
git checkout main
git pull origin main
git branch main-legacy
git push origin main-legacy
```

**Start migration on a feature branch:**
```bash
git checkout -b feature/minimal-improvements
```

**To rollback to legacy version:**
```bash
git checkout main-legacy
git checkout -b main
git push origin main --force
```

---

## 📝 Recommended Order of Implementation

1. Global exception handler & input validation
2. CORS and security improvements
3. *(Already completed)* Dockerization (Dockerfile & docker-compose), MySQL migration, profiles
4. Swagger/OpenAPI integration and README updates

---

## 💡 Benefits

- **Consistency:** Unified error handling, input validation, and JSON responses
- **Portability:** Dockerized & profile-driven; runs anywhere (local, CI, cloud)
- **Security:** Distinct public/admin APIs; JWT/role-based auth & robust CORS
- **Maintainability:** Lean, minimalistic codebase — easy upgrades, less glue code
- **Developer Experience:** Self-documented API (Swagger), clear onboarding & migration guide

---

## 📌 Notes

- **All secrets and credentials** must be provided only via environment variables / secret managers (`.env`, Docker/CI secrets).  
  _Never_ commit real passwords or database URLs to git!
- **Database dumps & migrations:** use UTF-8 encoding for error-free Cyrillic/special character import/export.
- This plan evolves: update architectural docs as the project matures.

---

This document summarizes the migration process, marks completed steps, defines next actions and best practices for a robust, maintainable, and secure Spring Boot news platform migrated from Drupal 6.

## Archived Branch: `main-legacy`

As of August 14, 2025, the `main-legacy` branch has been officially archived and locked for changes.  
It contains deprecated code and is preserved solely for historical reference.  
All active development now takes place in the `main` branch or other current branches.

🔗 Final commit before archival: [`8978e88`](https://github.com/rkonoplev/news-platform/commit/8978e8845a911aec1f2271e4e17f5013ef700efb)
