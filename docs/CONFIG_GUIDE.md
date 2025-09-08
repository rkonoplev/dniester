# ⚙️ Configuration Guide

This document explains the configuration strategy for the News Platform backend.  
Spring Boot profiles, YAML configuration files, and environment variables are used to keep the system secure, portable, and consistent across environments.

---

## 📂 Configuration Files Location

All application configuration is located under:

backend/src/main/resources/


- `application.yml` — base (global defaults, no secrets)
- `application-<profile>.yml` — profile-specific configuration overrides

Spring Boot chooses configuration based on the active profile (`SPRING_PROFILES_ACTIVE`).

---

## 🧩 Spring Profiles Matrix

| Profile | File                     | Database          | Schema Strategy      | Usage                                                                 |
|---------|--------------------------|-------------------|----------------------|----------------------------------------------------------------------|
| `local` | `application-local.yml`  | MySQL (Docker)    | `update`             | Local dev with `docker-compose`; uses `.env` for DB credentials.      |
| `dev`   | `application-dev.yml`    | MySQL/Postgres    | `update`             | Dev / staging; secrets injected via ENV (CI/CD).                      |
| `test`  | `application-test.yml`   | H2 (in-memory)    | `create-drop`        | Local & IDE tests; schema recreated automatically.                    |
| `ci`    | `application-ci.yml`     | H2 (in-memory)    | `create-drop`        | GitHub Actions CI; fast & isolated builds without external DB.        |
| `prod`  | `application-prod.yml`   | Cloud DB (MySQL/PG)| `none`              | Production (Render); config provided via Secrets (ENV/Secret Files).  |

**Note**: All profiles include rate limiting configuration (100 req/min public, 50 req/min admin).

---

## 🚀 Running with Profiles

Profiles can be set with either `SPRING_PROFILES_ACTIVE` environment variable or JVM arg:

```bash
# Run with dev profile
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun

# Run tests with CI profile
SPRING_PROFILES_ACTIVE=ci ./gradlew test

# Run production docker container
docker run -d -e SPRING_PROFILES_ACTIVE=prod --env-file .env.dev news-platform:latest
```
If no profile is specified, local is used as default.

## 🔐 Environment Variables & .env
Local Development (.env)
.env (ignored by git) provides DB and app credentials locally.
.env.example is included in the repo with placeholders (changemePass etc).

Example .env:
```# MySQL credentials
MYSQL_ROOT_PASSWORD=rootpass
MYSQL_DATABASE=newsdb
MYSQL_USER=newsuser
MYSQL_PASSWORD=newspass

# Ports
SPRING_LOCAL_PORT=8080
DATABASE_LOCAL_PORT=3306

# Spring datasource
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:${DATABASE_LOCAL_PORT}/newsdb?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
SPRING_DATASOURCE_USERNAME=${MYSQL_USER}
SPRING_DATASOURCE_PASSWORD=${MYSQL_PASSWORD}

# Authentication credentials (Basic Auth)
ADMIN_USERNAME=admin
ADMIN_PASSWORD=changemeAdmin
EDITOR_USERNAME=editor
EDITOR_PASSWORD=changemeEditor

# Rate limiting (optional, defaults applied if not set)
# PUBLIC_RATE_LIMIT=100
# ADMIN_RATE_LIMIT=50
```
⚠️ secrets must never be committed to git. Only .env.example goes into version control.

## 🔒 Secrets Management

### Authentication Security
- **Current**: Basic Auth with environment-based multi-user credentials
- **Role separation**: ADMIN (full access), EDITOR (content management), USER (public access)
- **Planned migration**: Google OAuth2 for all roles (ADMIN, EDITOR, USER) replacing Basic Auth
- **BCrypt encoding** for password security (current implementation)
- **No database storage** of authentication credentials (security best practice)
- User profile data stored in database, authentication handled separately

### Environment Management
- Local: .env file (ignored by git).
- CI/CD: GitHub Actions → repository Secrets.
- Production (Render): environment variables or Secret Files mounted at runtime.
- ✅ Passwords, tokens, and admin credentials are always injected via environment variables.
- ❌ Never hardcode secrets in application-*.yml or commit real credentials.

## ✅ Best Practices
- Keep application.yml as minimal defaults (no secrets).
- Use .env only for local/staging; add .env.example to repository as template.
- Production configs (prod profile) should only read from environment variables or secret files.
- Align database schema management per environment: update locally, validate/none in prod.
- Do not expose test credentials outside secure configs.
