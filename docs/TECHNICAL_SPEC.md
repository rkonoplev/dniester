# 📐 News Platform – Technical Specification
## 📑 Table of Contents
- [1. Project Summary](#1-project-summary)
- [2. Business Goals](#2-business-goals)
- [3. System Architecture](#3-system-architecture)
    - [Class-Level Structure](#class-level-structure)
- [4. Functional Requirements](#4-functional-requirements)
    - [4.1 Public API](#41-public-api-rate-limited-100-reqmin-per-ip)
    - [4.2 Admin API](#42-admin-api-rate-limited-50-reqmin-per-ip)
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
- **Frontend:** Next.js React application with Material UI (planned)

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


---

## 6. Technology Stack

| Area              | Backend Technology | Frontend Technology |
|-------------------|-------------------|--------------------|
| Language          | Java 21 | TypeScript/JavaScript |
| Framework         | Spring Boot | Next.js (React) |
| UI Library        | - | Material UI (MUI) |
| Database          | MySQL 8 (migrated from Drupal 6 DB) | - |
| ORM               | Hibernate / JPA | - |
| Build Tool        | Gradle | npm/yarn |
| API Docs          | OpenAPI / Swagger (springdoc) | - |
| Security          | Spring Security + Basic Auth | - |
| Rate Limiting     | Bucket4j (IP-based) | - |
| Deployment        | Docker + Render (PaaS) | Vercel/Netlify (planned) |
| CI/CD             | GitHub Actions | GitHub Actions |
| Code Quality      | JaCoCo | ESLint, Prettier |
| Static Checks     | Checkstyle, PMD | TypeScript |
| Testing           | JUnit 5, Testcontainers (planned) | Jest, React Testing Library |

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

### Backend

- Scheduled publishing
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