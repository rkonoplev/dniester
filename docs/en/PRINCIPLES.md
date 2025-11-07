# Design Principles in the Project

This project is designed with fundamental software development principles like SOLID, KISS, and YAGNI in mind.
This approach ensures a flexible, maintainable, and understandable architecture. This document describes
how these principles are applied in the codebase.

[Русская версия](./PRINCIPLES_RU.md)

---

## The SOLID Principles

SOLID is an acronym for five object-oriented design principles that help create systems that are
easier to maintain and extend.

### 1. S — Single Responsibility Principle

-   **Principle:** A class or component should have only one reason to change.
-   **Implementation in the project:** This principle is the foundation of the architecture. The code is
    clearly divided into layers (controllers, services, repositories), and each component has a single,
    well-defined responsibility.
    -   **`NewsController`** is only responsible for handling HTTP requests related to news.
    -   **`NewsService`** encapsulates business logic concerning only news.
    -   **`UserRepository`** is only responsible for data operations involving users.
    -   This separation prevents the emergence of "God classes" and simplifies code navigation.

### 2. O — Open/Closed Principle

-   **Principle:** Software entities should be open for extension but closed for modification.
-   **Implementation in the project:** This is evident in the use of the Spring Framework and its approaches.
    -   **Spring Security:** To add a new authentication method (e.g., OAuth 2.0), there is no need to
        modify existing code. Instead, a new configuration is created that **extends** the system.
    -   **Service Layer:** New functionality can be added through new methods or even new service classes
        that use existing ones without altering them.

### 3. L — Liskov Substitution Principle

-   **Principle:** Objects in a program should be replaceable with instances of their subtypes without
    altering the correctness of that program.
-   **Implementation in the project:** This is ensured by programming to interfaces.
    -   Services (`NewsService`) depend on repository interfaces (`NewsRepository`), not their concrete
        implementations. Spring Data JPA creates a proxy implementation at runtime that conforms to this
        interface. This allows the implementation to be swapped out (e.g., for testing) without changing
        the service code.

### 4. I — Interface Segregation Principle

-   **Principle:** "Clients should not be forced to depend on methods they do not use."
-   **Implementation in the project:** This is reflected in the creation of small, focused interfaces for
    each domain area.
    -   The `NewsService` interface contains only methods for working with news. It does not include methods
        for managing users or roles, which are segregated into `UserService` and `RoleService`. This
        prevents "fat" interfaces and makes dependencies between components more explicit.

### 5. D — Dependency Inversion Principle

-   **Principle:** High-level modules should not depend on low-level modules. Both should depend on
    abstractions. Abstractions should not depend on details. Details should depend on abstractions.
-   **Implementation in the project:** This is the central principle upon which the Spring Framework is built.
    -   **`NewsController`** (high-level) depends on the `NewsService` abstraction (interface), not on the
        concrete `NewsServiceImpl` class (low-level).
    -   Dependencies are not created within classes but are "injected" from an external source via the
        Dependency Injection mechanism (`@Autowired`). This inverts the control over dependencies, handing
        it to the framework.

---

## Pragmatic Principles

### KISS (Keep It Simple, Stupid)

This principle states that most systems work best if they are kept simple rather than made complicated;
therefore, simplicity should be a key goal in design, and unnecessary complexity should be avoided.

-   **Implementation in the project:**
    -   **Technology Stack Choice:** A classic, well-understood, and reliable stack (Spring Boot + JPA) was
        chosen for the core functionality. The more complex reactive approach (WebFlux + R2DBC) was deliberately
        postponed to avoid over-engineering in the early stages.
    -   **Straightforward Logic:** The business logic in the services is implemented as simply and clearly
        as possible, without excessive layers of abstraction or premature optimization.

### YAGNI (You Ain't Gonna Need It)

This principle of extreme programming states that functionality should not be added until it is
deemed necessary.

-   **Implementation in the project:**
    -   **Focus on MVP:** The project is focused on implementing the core functionality of a Headless CMS
        (content, user, and role management). Features like full-text search, file uploads, or Telegram
        integration were deferred to the "Technical Debt" as future tasks, rather than being implemented
        "just in case."
    -   **No Premature Optimization:** Performance optimization (e.g., migrating to a reactive stack) is
        planned only after real-world measurements reveal bottlenecks, not in advance.
