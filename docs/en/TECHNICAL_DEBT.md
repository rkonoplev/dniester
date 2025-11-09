# Technical Debt and Improvement History

This document tracks the history of significant improvements, current tasks, and future plans for the project.

**[Detailed Next.js Frontend Implementation Description here](../../frontends/nextjs/README.md).**

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

- **API Performance**: Analyze and optimize queries for high-traffic scenarios. SQL logging is now enabled
  in the `local` profile to facilitate diagnostics (e.g., finding N+1 problems).

---

## Future Plans

This section outlines planned features and improvements, categorized by priority.

### Security (High Priority)

- **OAuth 2.0 + JWT**: Transition from Basic Auth to a token-based authentication system.
  - **Purpose**: To implement a modern, secure, and scalable authentication mechanism.
  - **Components**: OAuth 2.0 grant flows, JWT generation and validation, token-based endpoint protection,
    refresh tokens, and updated login/logout procedures.

- **Two-Factor Authentication (2FA)**: Add a second layer of security for administrative roles.
  - **Purpose**: To enhance security for accounts with elevated privileges (e.g., ADMIN, EDITOR).
  - **Components**: Time-based One-Time Password (TOTP) generation and validation, integration with
    authenticator apps, and UI/UX for 2FA setup and confirmation.

### Functionality (Medium Priority)

- **File Uploads**: Implement support for managing images and other media.
  - **Purpose**: To allow users to upload files directly through the API.
  - **Components**: File storage service (e.g., local, S3, or MinIO), API endpoints for upload and retrieval.

- **Search Functionality**: Develop a search system for public and administrative parts of the application.
  - **Purpose**: To provide users with a fast and effective way to find content.
  - **Phase 1 (Angular & Next.js)**: Implement a basic, server-side search. The backend will expose a new API
    endpoint that uses SQL `LIKE` or `ILIKE` (for case-insensitivity in PostgreSQL) to search through
    article titles and content.
  - **Phase 2 (Next.js - Perspective)**: For the Next.js frontend, consider implementing a client-side
    search using **Pagefind**. This tool creates a static search index during the build process, allowing for
    instant, offline-capable search without any load on the backend API. This is a potential improvement
    to be evaluated after Phase 1 is complete.

- **Webhooks**: Develop a system for event-driven notifications.
  - **Purpose**: To notify external services of specific events within the application.
  - **Components**: A mechanism for managing webhook subscriptions and dispatching event payloads.

- **Telegram Integration**: Automatically post article previews to a Telegram channel.
  - **Purpose**: To expand content distribution and provide timely updates to subscribers.
  - **Components**: A Telegram Bot client, a service to listen for article creation/publication events,
    message formatting (preview, link), and secure storage for the bot token.

### Architecture & Performance (Low Priority)

- **Reactive Stack Migration**: Consider migrating public-facing, high-traffic endpoints to a non-blocking stack.
  - **Purpose**: To maximize scalability and resource efficiency under high load.
  - **Components**: Spring WebFlux and R2DBC for specific, performance-critical parts of the application.

### Implementation Sequence

1.  **Finalize Business Logic and API**: Complete and stabilize core application features.
2.  **Implement OAuth 2.0 + JWT**: Establish the primary authentication and authorization mechanism.
3.  **Update CI/CD and Deployment Configuration**: Ensure the deployment process is compatible with the new auth system.
4.  **Implement 2FA**: Add two-factor authentication for administrative roles as an additional security layer.

### Supporting Infrastructure and Services

- **Email Service (e.g., SMTP, Mailgun)**
  - **Purpose**: Required for user notifications, registration confirmation, and 2FA/password recovery.
- **File Storage (e.g., S3, MinIO)**
  - **Purpose**: For storing user-uploaded content like images and documents. To be evaluated based on final
    feature requirements.
- **Monitoring and Logging (Grafana Cloud)**
  - **Purpose**: Centralized application monitoring (Prometheus) and log aggregation (Loki). Recommended for
    production environments. Compatible with free-tier cloud hosting platforms as it does not require a local
    persistent disk.

### Reference Frontend Implementations

- **Status**: Currently in a basic structure phase. Full component implementation and API integration are
  required.
- **Next.js Frontend**: Implemented and ready for use.
- **Dependency**: Full implementation of the frontends will proceed after the final backend debugging,
  configuration, and verification. **Next.js frontend successfully integrated and tested with the backend.**
