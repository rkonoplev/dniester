# Architecture Migration Plan v.2

## Goals
- Minimal future intervention
- Low maintenance cost
- Stable operation on free-tier services
- Clean project structure without complex patterns (no DDD)

---

## Next Steps

### 1. Global Exception Handling

- Implement `@ControllerAdvice` to return consistent JSON errors for all exceptions.
- Standardize error response shape:
  ```json
  {
    "timestamp": "2025-08-14T12:00:00Z",
    "status": 400,
    "message": "Validation failed",
    "details": "/api/admin/news"
  }
  ```
### 2. Input Validation
   Add validation annotations (@NotNull, @Size, etc.) in DTOs like NewsCreateRequest and NewsUpdateRequest.
   Enable @Validated on controller classes.
   Handle MethodArgumentNotValidException in the global exception handler.

### 3. CORS and Security
   Extract CORS rules into a dedicated CorsConfig class for flexibility.
   Refine security configuration (roles/JWT) so admin endpoints are protected and public endpoints are open or use limited auth.

###  4.Dockerization
   Add a Dockerfile for the application.
   Create docker-compose.yml with service for Spring Boot app and MariaDB.
   Ensure a one-command startup:
   ```bash
   docker compose up -d
   ```
### 5. Profile-based Configuration
   Add application-dev.properties and application-prod.properties.
   Use Spring Profiles to keep database credentials, debug logging, and CORS flexible per environment.

### 6. API Documentation & README
   Integrate springdoc-openapi-ui to auto-generate Swagger UI.
   Add usage examples and Swagger link in the project README.
   Document endpoint testing workflow for developers and QA.

##  Branching and Git Safety
### 1. Archive the legacy branch

   ```shell
   git checkout main
   git pull origin main
   git branch main-legacy
   git push origin main-legacy
   ```
### 2. Start migration on a feature branch

   ```shell
   git checkout -b feature/migration-phase1
   ```
   Rollback Instructions
   To revert to a legacy codebase:

```shell
git checkout main-legacy
git checkout -b main
git push origin main --force
```

### Recommended Order of Implementation

1. Global exception handler and input validation
2. CORS and security improvements
3. Dockerization (Dockerfile & docker-compose.yml)
4. Profile-based configuration
5. Integrate Swagger/OpenAPI and update the README

### Benefits

- Consistency: Unified error handling and input validation.
- Portability: Dockerized setup for easy deployment and testing.
- Security: Proper distinction between public and admin APIs.
- Maintainability: Clean codebase, less glue code, and documented application contracts.
- Developer Onboarding: API documentation and setup procedures for rapid team growth.
