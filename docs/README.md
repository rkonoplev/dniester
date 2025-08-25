# 📖 Documentation

This directory contains all project documentation, organized into specific guides.

---

- [Developer Guide](./DEVELOPER_GUIDE.md)  
  Daily workflow for developers, local commands, how to run builds/tests in IntelliJ,  
  working without Docker, and CI/CD expectations.

- [Developer Guide (RU, plain text)](./DEVELOPER_GUIDE_RU.txt)  
  Russian text-only version of the developer guide: local workflow, Gradle/IDE commands,  
  CI/CD explanation. Useful for Russian-speaking developers who prefer plain text format.

- [Architecture Migration](./ARCHITECTURE_MIGRATION.md)  
  Overview of the migration from Drupal 6 → modern Spring Boot & Docker, completed steps, and next actions.

- [Configuration Guide](./CONFIG_GUIDE.md)  
  Spring profiles matrix (`local`, `dev`, `test`, `ci`, `prod`), `application-*.yml` files, `.env` usage, and secrets handling.

- [Docker Guide](./DOCKER_GUIDE.md)  
  Local development with `docker-compose`, production builds with Dockerfile, override files, and Render deployment notes.

- [CI/CD & Security](./CI_CD_SECURITY.md)  
  GitHub Actions workflow (`gradle-ci.yml`), automated testing, code quality tools (Checkstyle, PMD, JaCoCo), GitLeaks…

- [Technical Specification](./TECHNICAL_SPEC.md)  
  Functional and non-functional requirements, architecture layers, technology stack, and planned future enhancements.

- [API Usage Guide](./API_USAGE.md)  
  Example requests with `curl`, Makefile shortcuts for testing API endpoints, and usage instructions for developers & QA.

- [Migration Drupal6 → News Platform (EN)](./MIGRATION_DRUPAL6.md)  
  Full migration guide in English with all steps and TL;DR commands.

- [Migration Drupal6 → News Platform (RU, plain text)](./MIGRATION_DRUPAL6_RU.txt)  
  Russian plain-text migration guide for internal use (copyable into Word/Pages).

- [Database Schema](./DATABASE_SCHEMA.md)  
  Final MySQL 8 schema after migration, with ER model, DDL, and example queries.