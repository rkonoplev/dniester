# News Platform – Technical Specification

## Table of Contents
- [1. Project Summary](#1-project-summary)
- [2. Business Goals](#2-business-goals)
- [3. System Architecture](#3-system-architecture)
    - [Class-Level Structure](#class-level-structure)
- [4. Functional Requirements](#4-functional-requirements)
    - [4.1 Public API](#41-public-api)
    - [4.2 Admin API](#42-admin-api)
- [5. Data Migration](#5-data-migration)
- [6. Technology Stack](#6-technology-stack)
- [7. Non-functional Requirements (NFRs)](#7-non-functional-requirements-nfrs)
- [8. Development Workflow](#8-development-workflow)
- [9. Future Enhancements](#9-future-enhancements)
    - [Backend](#backend)
    - [Frontend](#frontend)


This document provides the technical specification for the **News Platform** project — a complete rewrite of the legacy Drupal 6 news system into a modern Java/Spring Boot application.

---

## 1. Project Summary
The **News Platform** is a complete modern news publishing system consisting of:
- **Backend:** Spring Boot REST API for content management and delivery
- **Frontend:** Angular application with Angular Universal (planned)

Designed to replace the legacy Drupal 6.0 stack with modern architecture for scalability and security.

The system provides:
- A **public API** for delivering news content to frontend and mobile clients
- An **admin API** for editorial teams to manage content and media
- A **responsive frontend** with SEO optimization and accessibility compliance
- Integration points for third-party services and future enhancements

---

## 2. Business Goals
1. Replace outdated Drupal backend with a modern system.
2. Improve editorial workflow with a simple CRUD interface.
3. Optimize performance (fast response times, scalable).
4. Enable future features:

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
    - **ADMIN** → full access to all endpoints and content
    - **EDITOR** → content management restricted to own authored articles
- CRUD functionality:
    - **ADMIN**: All articles (create, read, update, delete)
    - **EDITOR**: Own articles only (create, read own, update own, delete own)
- Publication control:
    - **ADMIN**: Publish/unpublish any article
    - **EDITOR**: Publish/unpublish own articles only
- Author-based security validation at service layer
- Stricter rate limiting for security.

---

## 5. Data Migration
- Import content from **Drupal 6 DB** into new schema:
    - `title`, `teaser`, `body`, `created_at`, `taxonomy`, `node_type`.
- Map legacy Drupal taxonomy to new category model.


---

## 6. Technology Stack

| Area              | Backend Technology | Frontend Technology |
|-------------------|-------------------|--------------------|
| Language          | Java 21 | TypeScript/JavaScript |
| Framework         | Spring Boot | Angular with Angular Universal |
| UI Library        | - | Angular Material |
| Database          | MySQL 8 (migrated from Drupal 6 DB) | - |
| ORM               | Hibernate / JPA | - |
| Caching           | Caffeine (In-Memory) | - |
| Build Tool        | Gradle | npm/yarn |
| API Docs          | OpenAPI / Swagger (springdoc) | - |
| Security          | Spring Security + Basic Auth (OAuth 2.0 + 2FA planned) | - |
| Rate Limiting     | Bucket4j (IP-based) | - |
| Deployment        | Docker + Render (PaaS) | Angular Universal SSR (planned) |
| CI/CD             | GitHub Actions | GitHub Actions |
| Code Quality      | JaCoCo | ESLint, Prettier |
| Static Checks     | Checkstyle, PMD | TypeScript |
| Testing           | JUnit 5, Testcontainers (planned) | Jasmine, Karma, Angular Testing Library |

---

## 7. Non-functional Requirements (NFRs)

- **Performance:**
    - Main endpoints ≤ 200ms response time under load.
- **Caching:**
    - **Implementation**: High-performance in-memory caching is implemented using Caffeine to reduce database load and improve response times for frequently accessed data.
    - **Strategy**: A multi-level caching strategy is employed:
        - **Default Cache**: A default TTL of 15 minutes and a maximum size of 1000 entries, used for general-purpose caching like individual news articles.
        - **Terms Cache**: A specialized cache for taxonomy terms with a longer TTL of 1 hour and a size of 500 entries, as this data changes infrequently.
        - **Search Cache**: A cache for search results with a shorter TTL of 5 minutes and a larger size of 2000 entries to balance data freshness with performance.
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

### Testing Strategy

#### Unit Tests
- **Mappers**: Entity ↔ DTO conversion
- **Services**: Business logic validation including bulk operations
- **BulkOperationsTest**: Role-based access control for bulk operations
- **Controllers**: Endpoint behavior (selective)

#### Integration Tests
- **Database**: H2 in-memory with MySQL mode
- **Repository**: Custom query methods and bulk operations
- **NewsRepositoryBulkIntegrationTest**: Bulk query methods testing
- **Security**: Isolated test configuration
- **Transactions**: `@Transactional` for cleanup

#### Test Configuration
- **Profiles**: Separate test profile with H2
- **Security**: Overridden with permissive config
- **Data**: Auto-generated test entities
- **Disabled Tests**: Controller integration tests for bulk operations (tested at service layer)

#### Test Coverage Notes
- **Bulk Operations**: Fully tested at service and repository layers
- **Role Restrictions**: ADMIN vs EDITOR access control verified
- **Controller Layer**: Thin HTTP adapter, core logic tested in service layer
- **Future**: Controller integration tests will be re-enabled with OAuth 2.0 + 2FA implementation

---

## 9. Future Enhancements

### Backend

- Analytics and usage metrics
- RSS/Atom feed integration
- WebSocket support for live updates
- Editor-friendly workflows (drafts, previews)

### Frontend
- Full-text search with auto-suggestions
- Dark mode theme toggle
- Lazy loading for images and infinite scroll on category pages
- Push notifications for breaking news
- Progressive Web App (PWA) features

---

**Author:** Roman Konoplev  
**Last Updated:** 2025-08-18