# 📐 News Platform – Technical Specification

This document provides the technical specification for the **News Platform** project — a complete rewrite of the legacy Drupal 6 news system into a modern Java/Spring Boot application.

---

## 1. Project Summary
The **News Platform** backend is a robust, maintainable service for publishing and managing news articles.  
It is designed to replace the legacy Drupal 6.0 stack with a modern architecture for scalability and security.

The backend provides:
- A **public API** for delivering news content to frontend and mobile clients.
- An **admin API** for editorial teams to manage content and media.
- Integration points for third-party services and future frontend apps.

---

## 2. Business Goals
1. Replace outdated Drupal backend with a modern system.
2. Improve editorial workflow with a simple CRUD interface.
3. Optimize performance (fast response times, scalable).
4. Enable future features:
    - multilingual support,
    - push notifications,
    - analytics integration.

---

## 3. System Architecture

- **Architecture Style:** RESTful API (stateless).
- **Primary Layers:**
    - **Controller Layer** → REST endpoints.
    - **Service Layer** → business logic & validation.
    - **Repository Layer** → Spring Data JPA persistence.
    - **DTO/Mapper Layer** → separates models and API contract.
- **Security Layer:** Spring Security + JWT authentication (role-based access).

### Class-Level Structure
- `controller/` – REST controllers (Public + Admin APIs).
- `service/` – application services with validation logic.
- `repository/` – database persistence via JPA.
- `dto/` – request/response objects.
- `entity/` – JPA entities (News, User, Term, Role).
- `mapper/` – Entity-DTO mapping.
- `config/` – Security, rate limiting, test configurations.
- `filter/` – Rate limiting filter with IP-based buckets.

---

## 4. Functional Requirements

### 4.1 Public API (Rate Limited: 100 req/min per IP)
- Retrieve news articles with pagination and sorting.
- Filter articles by category/term ID and publication date.
- Filter articles by multiple term IDs (checkbox-style filtering).
- Retrieve a single article by ID.
- Automatic rate limiting with response headers.

### 4.2 Admin API (Rate Limited: 50 req/min per IP)
- Secure access with Basic Authentication.
- Role-based authorization:
    - **ADMIN** → full access to all endpoints.
- CRUD functionality:
    - Articles (create, read, update, delete)
    - Published/unpublished content management
- Actions: **Publish/Unpublish** articles.
- Stricter rate limiting for security.

---

## 5. Data Migration
- Import content from **Drupal 6 DB** into new schema:
    - `title`, `teaser`, `body`, `created_at`, `taxonomy`, `node_type`.
- Map legacy Drupal taxonomy to new category model.
- Ensure UTF-8/utf8mb4 compatibility (for Cyrillic and multilingual content).

---

## 6. Technology Stack

| Area              | Technology |
|-------------------|------------|
| Language          | Java 21 |
| Framework         | Spring Boot |
| Database          | MySQL 8 (migrated from Drupal 6 DB) |
| ORM               | Hibernate / JPA |
| Build Tool        | Gradle |
| API Docs          | OpenAPI / Swagger (springdoc) |
| Security          | Spring Security + Basic Auth |
| Rate Limiting     | Bucket4j (IP-based) |
| Deployment        | Docker + Render (PaaS) |
| CI/CD             | GitHub Actions |
| Code Quality      | JaCoCo |
| Static Checks     | Checkstyle, PMD |
| Testing           | JUnit 5, Testcontainers (planned) |

---

## 7. Non-functional Requirements (NFRs)

- **Performance:**
    - Main endpoints ≤ 200ms response time under load.
- **Scalability:**
    - Horizontal scalability for >10k concurrent users.
- **Security:**
    - OWASP Top 10 compliance.
    - Secrets managed via `.env`, GitHub Secrets, Render Secrets.
- **Maintainability:**
    - >80% unit test coverage (JaCoCo).
    - Automated static analysis (Checkstyle, PMD).
- **Documentation:**
    - API self-documented with Swagger UI.
    - Clear onboarding docs in `/docs`.

---

## 8. Development Workflow

- **Branching Model:** Git Flow (main, develop, feature/*).
- **Code Reviews:** All PRs reviewed before merge.
- **Testing:** CI runs tests with `ci` profile (H2 in-memory DB).
- **Deployment:**
    - CI/CD via GitHub Actions.
    - Production deploy on Render.
    - Secrets injected via environment variables or secret files.

---

## 9. Future Enhancements

- Multilingual support for articles.
- Scheduled publishing.
- Analytics and usage metrics.
- RSS/Atom feed integration.
- WebSocket support for live updates.
- Editor-friendly workflows (drafts, previews).

---

**Author:** Roman Konoplev  
**Last Updated:** 2025-08-18