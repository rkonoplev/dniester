# Technical Debt and Improvement History

This document tracks the history of significant improvements, current tasks, and future plans for the project.

---

## Completed Tasks and Architectural Decisions

This section serves as a changelog, documenting key implemented features and refactorings based on an analysis
of the current codebase.

### Architecture & Design
- **Layered Architecture**: A clear separation between Controller, Service, and Repository layers has been
  implemented.
- **Single Responsibility Principle**: Logic is separated by domain (news, roles, authorization).
- **DTO Pattern**: Data Transfer Objects are used for all API requests and responses, ensuring the internal
  model is decoupled.
- **Centralized Authorization**: Access control logic has been extracted into a dedicated `AuthorizationService`.
- **Automated Mapping**: MapStruct is integrated for automatic conversion between DTOs and entities.

### Security
- **Authentication & Authorization**: Spring Security is integrated with Basic Authentication.
- **Role-Based Access Control (RBAC)**: A system with roles (ADMIN, EDITOR) and granular permissions is
  implemented.
- **Endpoint Protection**: Administrative APIs are secured and require appropriate roles.

### Performance
- **Caching**: Method-level caching (`@Cacheable`) is implemented using Caffeine for frequently accessed data.
- **Rate Limiting**: API flood protection is implemented using Bucket4j.
- **Transaction Optimization**: All read-only service methods are annotated with `@Transactional(readOnly = true)`.

### Database
- **Data Access**: The project currently uses the blocking **Spring Data JPA** stack for simplicity and reliability.
- **Migration Management**: The database schema is version-controlled using Flyway.
- **Multi-Database Support**: The architecture supports both MySQL and H2 (for testing).

### Code Quality & CI/CD
- **Static Analysis**: Checkstyle and PMD are configured and integrated to maintain code quality.
- **Test Coverage**: JaCoCo is integrated for code coverage analysis.
- **Test Structure Refactoring**: A full separation of tests into `unit/` and `integration/` directories has
  been completed.
- **Gradle Configuration**: `build.gradle` is configured to run unit and integration tests separately.
- **Integration Tests**: Configured to work with docker-compose MySQL using `local` profile with Hibernate
  `create-drop` for automatic schema management.
- **Unified Test Architecture**: Created a single `AbstractIntegrationTest` base class for all integration
  tests, eliminating code duplication.

---

## In Progress

- **API Performance**: Further query optimization for high-traffic scenarios.

---

## Future Plans

This section outlines planned features and improvements, categorized by priority.

### Security (High Priority)

#### 1. OAuth 2.0 + JWT — Replacing Basic Auth
- **Goal**: Transition from the outdated Basic Auth to a modern, secure, and scalable authentication system.
- **Features**:
  - Authorization via OAuth 2.0 (e.g., Google, GitHub, or a custom Identity Provider).
  - Generation and validation of JWT tokens.
  - Storing roles and permissions within the token or in the database.
  - Updating login, endpoint protection, logout, and refresh token mechanisms.
- **When to Implement**: After stabilizing the business logic and API, but before the public release.
- **Recommendation**: It is advisable to implement this before deploying to Render.com to avoid changing the authorization mechanics in production.

#### 2. 2FA (Two-Factor Authentication) for ADMIN/EDITOR
- **Goal**: Add an extra layer of security for critically important roles.
- **Features**:
  - Generation of temporary codes (TOTP, SMS, email).
  - Support for Google Authenticator or similar apps.
  - UI/UX for second-factor confirmation.
  - Storage and verification of 2FA secrets.
- **When to Implement**: After implementing OAuth 2.0 + JWT, when the main authorization system is secure and flexible.
- **Note**: This is an add-on that requires a stable token and role system.

### Recommended Action Plan

| Stage | Action | Rationale |
| :---: | :--- | :--- |
| 1️⃣ | Finalize current business logic and API | To avoid conflicts with authorization changes |
| 2️⃣ | Implement OAuth 2.0 + JWT | The core authentication mechanism |
| 3️⃣ | Update CI/CD and Render configuration | To ensure deployment works with the new auth |
| 4️⃣ | Implement 2FA for ADMIN/EDITOR | To enhance security |

### Other Potential Components

| Component | Purpose | Needed? |
| :--- | :--- | :---: |
| Email service (SMTP, Mailgun) | Notifications, registration confirmation | ✅ Yes |
| OAuth 2.0 + JWT | Secure authentication | ✅ Yes |
| 2FA | Admin protection | ✅ Yes |
| File storage (S3, MinIO) | Storing images, documents | ✅ Possibly |
| Monitoring (Prometheus + Grafana) | Application state tracking | ✅ In the future |
| Logging (ELK, Loki) | Centralized logs | ✅ In the future |

**Recommendation for this project:**
For an administration site or news portal:
- **Prometheus + Grafana** for monitoring (CPU, RAM, errors, alerts).
- **Loki + Grafana** for logging (errors, requests, user actions).
- **ELK Stack** if powerful log search is needed, but Loki is simpler and lighter.

### Functionality (Medium Priority)

- **File Upload**: Implement support for uploading and managing images and other media directly through the API.
- **Advanced Search**: Integrate a full-text search engine like Elasticsearch or Lucene.
- **Webhooks**: Provide a system for sending event-driven notifications to external services.

### Architecture & Performance (Low Priority)

- **Reactive Stack Migration**: For high-traffic public endpoints, consider migrating from the blocking
  Spring MVC/JPA stack to a non-blocking, reactive stack (Spring WebFlux + R2DBC) to maximize
  scalability and resource efficiency.

### Reference Frontend Implementations

- **Status**: Currently in a basic structure phase. Full component implementation and API integration are
  required.
- **Dependency**: Full implementation of the frontends will proceed after the final backend debugging,
  configuration, and verification.
