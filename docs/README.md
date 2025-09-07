# 📖 Documentation

This directory contains all project documentation, organized into specific guides.

---

- **[Complete Project Information for AI Analysis](./TASK_DESCRIPTION.md)**  
  Comprehensive project overview designed for AI platforms and new developers. Contains complete architecture,  
  technology stack, database schema, API endpoints, configuration details, and development workflow.  
  Perfect for quickly understanding the entire project structure and technical decisions.

- **[Quick Start (Russian)](./QUICK_START_RU.md)**  
  Краткая инструкция по запуску проекта для русскоязычных разработчиков. Включает быстрый старт,  
  решение типовых проблем и основные команды для разработки.

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

- [Frontend Specification](./FRONTEND_SPEC.md)  
  Frontend technical specification with Next.js, Material UI, responsive design, SEO requirements, and theming guidelines.

- [Admin Panel Specification](./ADMIN_PANEL_SPEC.md)  
  Technical requirements for the admin panel interface including role management, news management, taxonomy terms, bulk actions, and security validation.

- [API Usage Guide](./API_USAGE.md)  
  Example requests with `curl`, Makefile shortcuts for testing API endpoints, and usage instructions for developers & QA.

- [Migration Drupal6 → News Platform (EN)](./MIGRATION_DRUPAL6.md)  
  Full migration guide in English with all steps and TL;DR commands.

- [Migration Drupal6 → News Platform (RU, plain text)](./MIGRATION_DRUPAL6_RU.txt)  
  Russian plain-text migration guide for internal use (copyable into Word/Pages).

- [Database Schema](./DATABASE_SCHEMA.md)  
  Final MySQL 8 schema after migration, with ER model, DDL, and example queries.

- [Rate Limiting Guide](./RATE_LIMITING.md)  
  IP-based rate limiting implementation with Bucket4j, configuration options, testing procedures, and production considerations.