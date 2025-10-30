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
- **Testcontainers Integration**: Testcontainers have been implemented for robust integration testing with a
  real DB (MySQL), ensuring an isolated and clean test environment.

---

## In Progress

- **API Performance**: Further query optimization for high-traffic scenarios.

---

## Future Plans

This section outlines planned features and improvements, categorized by priority.

### Security (High Priority)

- **OAuth 2.0 + JWT**: Replace Basic Auth with modern, token-based authentication for enhanced security.
- **2FA for ADMIN/EDITOR**: Implement two-factor authentication for critical user roles.

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
