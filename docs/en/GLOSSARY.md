> [Back to Documentation Contents](./README.md)

# Glossary of Terms

This glossary provides brief explanations of key terms and technologies used in the Phoebe CMS
project documentation.

---

## General Terms

-   **JDK (Java Development Kit)**: A software development environment used for developing Java
    applications. It includes the Java Runtime Environment (JRE), an interpreter/loader (java),
    a compiler (javac), an archiver (jar), and other tools.

-   **Docker**: A platform for developing, shipping, and running applications in containers.
    Containers are lightweight, standalone, executable packages of software that include everything
    needed to run an application: code, runtime, system tools, system libraries, and settings.

-   **Docker Compose**: A tool for defining and running multi-container Docker applications.
    With Compose, you use a YAML file to configure your application's services.

-   **Git**: A distributed version control system for tracking changes in source code during
    software development. It's designed for coordinating work among programmers, but it can be used
    to track changes in any set of files.

-   **CI/CD (Continuous Integration/Continuous Delivery)**: A method to deliver apps frequently
    to customers by introducing automation into the stages of app development. CI involves frequently
    merging code changes into a central repository, while CD automates the delivery of changes
    to production.

-   **Headless CMS**: A content management system that provides a backend-only content repository.
    It makes content accessible via an API for display on any device or platform, without a
    predefined frontend.

-   **ETL (Extract, Transform, Load)**: A three-phase process used to move data from one source
    to another. Extract (read data from source), Transform (convert data to fit target system),
    Load (write data to target system).

-   **Makefile**: A utility that automates the compilation and building of software from source
    code. It uses a `Makefile` to define rules and dependencies between files, simplifying the
    execution of complex command sequences.

-   **DTO (Data Transfer Object)**: An object used to transfer data between application layers
    or between an application and a client. It is designed to encapsulate data and reduce the
    number of calls to remote interfaces.

-   **XSS (Cross-Site Scripting)**: A type of web security vulnerability that enables attackers
    to inject malicious client-side scripts into web pages viewed by other users.

---

## Backend & Java Specific Terms

-   **Spring Boot**: An open-source, Java-based framework used to create stand-alone,
    production-grade Spring applications with minimal configuration. It simplifies the setup
    and development of Spring-based applications.

-   **Spring Security**: A powerful and customizable authentication and access-control framework
    for Spring-based applications. It provides comprehensive security solutions for enterprise-level
    applications.

-   **Spring Profiles**: A feature in Spring Framework that allows you to register different beans
    for different environments (e.g., `dev`, `test`, `prod`). Configurations for profiles are
    typically defined in `application-{profile}.yml` files.

-   **Gradle**: A build automation tool used for multi-language language software development. It controls
    the development process in terms of compilation, packaging, and testing.

-   **Flyway**: An open-source database migration tool. It helps you manage database schema changes
    by versioning your database and applying migrations in a structured way.

-   **Flyway Migrations (V-scripts)**: SQL scripts used by Flyway to version and apply changes
    to the database schema. They are named with a `V` prefix and a version number
    (e.g., `V1__initial_schema.sql`).

-   **JPA (Java Persistence API)**: A Java API specification for managing relational data in
    applications using Java Platform, Standard Edition (Java SE) and Java Platform, Enterprise
    Edition (Java EE). It allows developers to map Java objects to database tables.

-   **REST API (Representational State Transfer Application Programming Interface)**: An
    architectural style for designing networked applications. REST APIs use standard HTTP methods
    (GET, POST, PUT, DELETE) to interact with resources.

-   **Swagger / OpenAPI**: A set of tools and specifications for describing, producing, consuming,
    and visualizing RESTful web services. OpenAPI Specification (OAS) is a standard, language-agnostic
    interface for REST APIs.

-   **BCrypt**: A password-hashing function designed to slow down brute-force attacks. It is used
    to securely store passwords by adding a salt and performing multiple rounds of hashing.

---

## Testing Specific Terms

-   **Testcontainers**: A Java library that allows you to run real services (databases, message
    brokers, web browsers) in Docker containers for integration tests. It provides an isolated
    and reproducible testing environment.

---

## Drupal & Migration Specific Terms

-   **CCK (Content Construction Kit)**: A module in older versions of Drupal (like Drupal 6) that
    allowed users to add custom fields to content types, extending their functionality beyond basic
    title and body fields.

---

## Code Quality & Security Tools

-   **Checkstyle**: A development tool to help programmers write Java code that adheres to a coding
    standard. It automates the checking of Java code against a set of predefined rules.

-   **PMD**: A static source code analyzer that finds common programming flaws like unused variables,
    empty catch blocks, unnecessary object creation, and so forth. It also includes a copy/paste
    detector.

-   **JaCoCo (Java Code Coverage)**: A free code coverage library for Java. It provides code coverage
    metrics for unit tests and helps identify untested parts of the code.

-   **GitLeaks**: A tool that scans Git history (commits and repositories) for secrets and sensitive
    information (e.g., API keys, passwords, tokens) that might have been accidentally committed.

-   **Bucket4j**: A Java library for implementing rate limiting using the "Token Bucket" algorithm.
    It allows controlling the number of requests a client can send within a defined period.
