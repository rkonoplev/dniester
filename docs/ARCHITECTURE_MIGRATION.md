Here's the `docs/ARCHITECTURE_MIGRATION.md` file with a clear migration plan, structure, and rollback commands:

# Architecture Migration Plan

## Goals
- Minimal future intervention
- Low maintenance cost
- Stable operation on free-tier services
- Clean project structure without complex patterns (no DDD)

## Recommended Improvements

### 1. Separate Public and Admin APIs
- `AdminNewsController` → `/api/admin/news`
- `PublicNewsController` → `/api/public/news`

### 2. Global Exception Handler
- Implement `@ControllerAdvice` for consistent JSON error responses
- Response format:
```json
{
  "timestamp": "2025-08-14T12:00:00Z",
  "status": 400,
  "message": "Validation failed",
  "details": "/api/admin/news"
}
```

### 3. CORS and Security Configuration
- Extract CORS settings to dedicated `CorsConfig`
- Simplifies frontend changes without security rewrites

### 4. Input Validation
- Enable `@Validated` in controllers
- Handle `MethodArgumentNotValidException` in `@ControllerAdvice`

### 5. Docker + Docker Compose
- Single `docker-compose.yml` for project + database
- One-command startup: `docker compose up -d`

### 6. Profile-based Configuration
- `application-dev.properties` for development
- `application-prod.properties` for production

### 7. README + Swagger
- Add Swagger UI link (via `springdoc-openapi-ui`)
- Document API testing procedures

## Git Preservation Procedure

### 1. Save current state
```bash
git checkout main
git pull origin main
git branch main-legacy
git push origin main-legacy
```

### 2. Create improvement branch
```bash
git checkout -b feature/minimal-improvements
```

## Rollback Procedure

### 1. Switch to preserved branch
```bash
git checkout main-legacy
```

### 2. Make it the new main branch (if needed)
```bash
git checkout -b main
git push origin main --force
```

## Implementation Order

1. Split API routes (item 1)
2. Create `@ControllerAdvice` (items 2, 4)
3. Extract CORS config (item 3)
4. Implement Docker Compose (item 5)
5. Add profile configurations (item 6)
6. Update README + Swagger (item 7)

## Notes
After implementing all items, the project will be:
- Easier to maintain
- Simpler to migrate to new hosting
- More consistent in error handling
- Better documented for new developers
```

This document provides:
1. Clear goals for the migration
2. Specific technical improvements
3. Step-by-step Git commands for preservation and rollback
4. Logical implementation order
5. Justification for each change

The structure follows best practices for architectural documentation while keeping it concise and actionable.

## Archived Branch: `main-legacy`

As of August 14, 2025, the `main-legacy` branch has been officially archived and locked for changes.  
It contains deprecated code and is preserved solely for historical reference.  
All active development now takes place in the `main` branch or other current branches.

🔗 Final commit before archival: [`8978e88`](https://github.com/rkonoplev/news-platform/commit/8978e8845a911aec1f2271e4e17f5013ef700efb)
