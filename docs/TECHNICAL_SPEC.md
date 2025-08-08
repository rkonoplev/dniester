# News Platform Backend – Technical Specification

## 1. Project Summary
The **News Platform Backend** is a complete rewrite of a legacy Drupal 6.0-based news portal.  
The goal is to modernize the platform using **Java 17** and **Spring Boot**, while preserving existing content and improving maintainability, scalability, and security.

This backend will power:
- A **public-facing API** for serving news articles.
- An **admin panel** for managing content and media.
- Integration-ready endpoints for future mobile or frontend apps.

---

## 2. Business Goals
1. Replace outdated Drupal backend with a modern, maintainable architecture.
2. Improve editorial workflow with intuitive CRUD interfaces.
3. Optimize page load and API response times for better user experience.
4. Enable future features like push notifications, multilingual support, and analytics.

---

## 3. System Architecture
- **Architecture Style:** RESTful API (stateless).
- **Layers:**
    - **Controller Layer** – Handles HTTP requests/responses.
    - **Service Layer** – Business logic and validation.
    - **Repository Layer** – Database access via Spring Data JPA.
- **Security Layer:** JWT-based authentication + role-based access control.

---

## 4. Functional Requirements

### 4.1 Public API
- Retrieve latest news with pagination and sorting.
- Filter by category (taxonomy) and publication date.
- Retrieve a single article by ID or slug.
- Search endpoint for article titles and content.

### 4.2 Admin Panel
- Authentication required.
- Role-based permissions:
    - **Admin** – Full access.
    - **Editor** – Can create, edit, and publish articles.
- CRUD operations for:
    - Articles
    - Categories
    - Media files
- Image upload with automatic resizing and optimization.
- Action buttons:
    - Publish / Unpublish
    - Mark as Featured

---

## 5. Data Migration
- Import from legacy Drupal DB:
    - `title`, `teaser` (HTML-supported), `body`, `created_at`, `taxonomy`, `node_type`.
- Map old taxonomy terms to the new schema.

---

## 6. Technology Stack
| Area              | Technology |
|-------------------|------------|
| Language          | Java 17 |
| Framework         | Spring Boot |
| Database          | MariaDB |
| ORM               | Hibernate / JPA |
| Build Tool        | Gradle |
| API Docs          | OpenAPI/Swagger |
| Security          | Spring Security + JWT |
| Deployment        | Docker |
| CI/CD             | GitHub Actions |
| Code Quality      | JetBrains Qodana |
| Testing           | JUnit 5 |

---

## 7. Non-functional Requirements
- **Performance:** Main endpoints respond in under 200ms.
- **Scalability:** Can handle 10k concurrent users with horizontal scaling.
- **Security:** OWASP Top 10 compliance.
- **Code Quality:** Automated checks with Qodana + unit test coverage >80%.
- **Documentation:** API self-documentation via Swagger UI.

---

## 8. Development Workflow
- **Branching Model:** `main` (production), `dev` (integration), feature branches.
- **Code Review:** PRs required before merging to `main`.
- **Testing:** Automated tests run on every push.
- **Deployment:** CI/CD pipeline builds Docker image and deploys to staging/production.

---

## 9. Future Enhancements
- Multilingual article support.
- Scheduled publishing.
- Integration with analytics tools.
- RSS and Atom feeds.
- WebSockets for live updates.

---

**Author:** Roman Konoplev  
**Last Updated:** 2025-08-08
