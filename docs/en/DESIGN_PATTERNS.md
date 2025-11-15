# Design Patterns in the Project

This project utilizes a range of established design patterns to ensure a flexible, scalable, and maintainable
codebase. This document describes the key patterns and their specific implementation within the system.

[Русская версия](./DESIGN_PATTERNS_RU.md)

---

## 1. Creational Patterns

These patterns are responsible for object creation.

-   **Singleton**
    -   **Implementation:** Applied by the Spring Framework to all components managed by the IoC container
        (annotated with `@Service`, `@Repository`, `@Controller`, `@Component`). Spring by default
        creates only one instance (bean) of each component, which is reused throughout the application.
    -   **Purpose:** Ensures state consistency and resource efficiency. In the context of data,
        a Singleton pattern can also be applied to entities that represent a unique,
        system-wide configuration.
    -   **Example (Data Singleton):** The `ChannelSettings` entity is designed to hold global, unique settings
        for the entire CMS (e.g., site title, meta tags, footer HTML). Conceptually, there should only ever
        be one record of `ChannelSettings` in the database, making it a data-level Singleton. The
        application's service layer ensures this uniqueness by always retrieving and updating this single record.

-   **Factory Method / Abstract Factory**
    -   **Implementation:** Used implicitly by the Spring IoC container.
    -   **Purpose:** The Spring container acts as a factory that creates, configures, and injects objects
        (beans) on demand (e.g., via `@Autowired`). This relieves components from the responsibility of
        creating their own dependencies.

-   **Builder**
    -   **Implementation:** Recommended for use when creating complex objects, especially DTOs and entities
        in unit and integration tests. For example, using Lombok `@Builder`.
    -   **Purpose:** Allows for the step-by-step construction of objects, making the code more readable and safer
        compared to constructors with a large number of parameters.

---

## 2. Structural Patterns

These patterns deal with object and class composition.

-   **Proxy**
    -   **Implementation:** Used extensively by Spring AOP (Aspect-Oriented Programming) to implement
        cross-cutting concerns.
    -   **Purpose:** Spring creates proxy objects around beans to add extra logic before, after, or around
        method calls. Prime examples include the `@Transactional` (transaction management), `@Cacheable`
        (caching), and Spring Security mechanisms.

-   **Adapter**
    -   **Implementation:** Partially realized through the use of DTOs (Data Transfer Objects) and mappers (e.g., MapStruct).
    -   **Purpose:** DTOs adapt the internal data structure (JPA entities) to the format required by
        external clients (API responses), hiding internal details and preventing their leakage. Mappers
        help automate this adaptation process.

---

## 3. Behavioral Patterns

These patterns are concerned with communication between objects.

-   **Strategy**
    -   **Implementation:** Forms the basis of many Spring components, especially Spring Security, and
        can also be implemented for selecting different data processing algorithms.
    -   **Purpose:** Allows for defining a family of algorithms, encapsulating each one, and making them
        interchangeable. For example, the authentication mechanism can be easily switched from Basic Auth
        to OAuth 2.0 by substituting one "strategy" for another.

-   **Template Method**
    -   **Implementation:** Used in Spring classes like `JdbcTemplate` and `RestTemplate`.
    -   **Purpose:** Defines the skeleton of an algorithm in an operation, deferring some steps to
        subclasses. This avoids code duplication for standard operations (e.g., opening/closing
        connections, exception handling).

---

## 4. Architectural Patterns and Idioms

-   **Layered Architecture**
    -   **Implementation:** The foundation of the application's structure. The code is clearly divided into layers:
        -   `@Controller` (Presentation Layer)
        -   `@Service` (Business Logic/Service Layer)
        -   `@Repository` (Data Access Layer)
    -   **Purpose:** Enforces separation of concerns, improving testability and maintainability.

-   **Dependency Injection (DI) / Inversion of Control (IoC)**
    -   **Implementation:** A core principle of the Spring Framework, implemented via the `@Autowired` annotation.
    -   **Purpose:** Components do not create their dependencies but receive them from an external source (the IoC
        container). This reduces code coupling and simplifies unit testing.

-   **Data Transfer Object (DTO)**
    -   **Implementation:** Used for all incoming API requests and outgoing responses.
    -   **Purpose:** Decouples the internal data model (JPA Entities) from the external representation (API),
        enhancing system security and flexibility.

-   **Repository**
    -   **Implementation:** Applied through Spring Data JPA interfaces (e.g., `JpaRepository`).
    -   **Purpose:** Abstracts the data access layer from the rest of the application, hiding the implementation
        details of database queries.
