# Documentation

This directory contains all English project documentation for **Phoebe CMS**.

> **Русская документация**: See [../ru/](../ru/) for Russian documentation.

---

## 📖 Core Documentation

- **[Project Overview](./PROJECT_OVERVIEW.md)**  
  Comprehensive project overview designed for new developers. Contains complete architecture,  
  technology stack, business goals, migration status, and technical roadmap.  
  Perfect for quickly understanding the entire Headless CMS structure and technical decisions.

- **[Developer Guide](./DEVELOPER_GUIDE.md)**  
  Daily workflow for developers, local commands, how to run builds/tests in IntelliJ,  
  working without Docker, and CI/CD expectations for the headless backend.

- **[API Reference](./API_REFERENCE.md)**  
  Complete REST API documentation with `curl` examples, Makefile shortcuts for testing endpoints,  
  and usage instructions for frontend developers integrating with the headless CMS.

## 🏗️ Architecture & Setup

- **[Configuration Guide](./CONFIG_GUIDE.md)**  
  Spring profiles matrix (`local`, `dev`, `test`, `ci`, `prod`), `application-*.yml` files,  
  `.env` usage, and secrets handling for headless deployment.

- **[Docker Guide](./DOCKER_GUIDE.md)**  
  Local development with `docker-compose`, production builds with Dockerfile,  
  override files, and cloud deployment notes for the headless backend.

## 🔐 Security & Authentication

- **[Authentication Guide](./AUTHENTICATION_GUIDE.md)**  
  Security implementation for the headless API, authentication methods,  
  and integration guidelines for frontend applications.

- **[Security & Roles](./SECURITY_ROLES.md)**  
  Comprehensive guide for implementing ADMIN and EDITOR role restrictions  
  with author-based security in the headless CMS.

- **[Rate Limiting Guide](./RATE_LIMITING.md)**  
  IP-based rate limiting implementation with Bucket4j, configuration options,  
  testing procedures, and production considerations for API protection.

## 🎨 Frontend Integration

- **[Frontend Specification](./FRONTEND_SPEC.md)**  
  Reference frontend technical specification with Angular, responsive design,  
  SEO requirements, and guidelines for building custom frontends.

- **[Admin Panel Specification](./ADMIN_PANEL_SPEC.md)**  
  Technical requirements for admin panel interfaces including role management,  
  content management, taxonomy, and bulk operations via the headless API.

## 🗄️ Database & Migration

- **[Database Guide](./DATABASE_GUIDE.md)**  
  Complete database documentation including MySQL 8 schema, setup procedures, migration scripts,  
  Spring Boot migrations (V1-V6), and troubleshooting for both clean and migrated installations.

- **[Migration Drupal6 → Phoebe CMS](./MIGRATION_DRUPAL6.md)**  
  Complete migration guide from legacy Drupal 6 to modern headless architecture  
  with all steps and TL;DR commands.

## 🛠️ Development Tools

- **[CI/CD Guide](./CI_CD_GUIDE.md)**  
  GitHub Actions workflow (`gradle-ci.yml`), automated testing, code quality tools  
  (Checkstyle, PMD, JaCoCo), GitLeaks, and security scanning for the headless backend.

- **[Code Style Setup](./CODE_STYLE_SETUP.md)**  
  Complete guide for automatic code formatting in IntelliJ IDEA with 120-character line length,  
  actions on save configuration, Checkstyle integration, and troubleshooting tips.

- **[Input Validation Guide](./VALIDATION_GUIDE.md)**  
  Comprehensive validation and content processing guide including SafeHtml validation,  
  YouTube embed support, XSS protection, and entity validation rules.



---

## 🌐 Headless CMS Benefits

This documentation supports **Phoebe's hybrid headless approach**:

- **API-First**: Complete REST API documentation for custom frontend development.
- **Reference Implementation**: Guides for using the provided Angular frontend.
- **Flexibility**: Choose between headless-only or full-stack deployment.
- **Professional Grade**: Enterprise-ready features for any organization.

For Russian documentation, visit [../ru/](../ru/).