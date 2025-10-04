# Documentation

This directory contains all English project documentation for **Phoebe CMS**.

> **Русская документация**: See [../ru/](../ru/) for Russian documentation.

---

## 📖 Core Documentation

- **[Complete Project Information](./TASK_DESCRIPTION.md)**  
  Comprehensive project overview designed for new developers. Contains complete architecture,  
  technology stack, database schema, API endpoints, configuration details, and development workflow.  
  Perfect for quickly understanding the entire Headless CMS structure and technical decisions.

- **[Developer Guide](./DEVELOPER_GUIDE.md)**  
  Daily workflow for developers, local commands, how to run builds/tests in IntelliJ,  
  working without Docker, and CI/CD expectations for the headless backend.

- **[API Usage Guide](./API_USAGE.md)**  
  Complete REST API documentation with `curl` examples, Makefile shortcuts for testing endpoints,  
  and usage instructions for frontend developers integrating with the headless CMS.

## 🏗️ Architecture & Setup

- **[Architecture Migration](./ARCHITECTURE_MIGRATION.md)**  
  Overview of the migration from Drupal 6 → modern Spring Boot headless architecture,  
  completed steps, and roadmap for hybrid headless implementation.

- **[Configuration Guide](./CONFIG_GUIDE.md)**  
  Spring profiles matrix (`local`, `dev`, `test`, `ci`, `prod`), `application-*.yml` files,  
  `.env` usage, and secrets handling for headless deployment.

- **[Docker Guide](./DOCKER_GUIDE.md)**  
  Local development with `docker-compose`, production builds with Dockerfile,  
  override files, and cloud deployment notes for the headless backend.

- **[Technical Specification](./TECHNICAL_SPEC.md)**  
  Functional and non-functional requirements, headless architecture layers,  
  technology stack, and planned future enhancements.

## 🔐 Security & Authentication

- **[Authentication Guide](./AUTHENTICATION_GUIDE.md)**  
  Security implementation for the headless API, authentication methods,  
  and integration guidelines for frontend applications.

- **[Role Security Implementation](./ROLE_SECURITY_IMPLEMENTATION.md)**  
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

- **[Database Schema](./DATABASE_SCHEMA.md)**  
  Final MySQL 8 schema for the headless CMS, with ER model, DDL,  
  and example queries for content management.

- **[Migration Drupal6 → Phoebe CMS](./MIGRATION_DRUPAL6.md)**  
  Complete migration guide from legacy Drupal 6 to modern headless architecture  
  with all steps and TL;DR commands.

## 🛠️ Development Tools

- **[CI/CD & Security](./CI_CD_SECURITY.md)**  
  GitHub Actions workflow (`gradle-ci.yml`), automated testing, code quality tools  
  (Checkstyle, PMD, JaCoCo), GitLeaks, and security scanning for the headless backend.

- **[Code Style Setup](./CODE_STYLE_SETUP.md)**  
  Complete guide for automatic code formatting in IntelliJ IDEA with 120-character line length,  
  actions on save configuration, Checkstyle integration, and troubleshooting tips.

## 📋 Legal Information

- **[Legal Disclaimer](./DISCLAIMER.md)**  
  Warranty disclaimers, AI-generated content notice, and legal responsibility information.

- **[Third-Party Licenses](./NOTICE.md)**  
  Complete list of open-source dependencies and their respective licenses.

---

## 🌐 Headless CMS Benefits

This documentation supports **Phoebe's hybrid headless approach**:

- **API-First**: Complete REST API documentation for custom frontend development
- **Reference Implementation**: Guides for using the provided Angular frontend
- **Flexibility**: Choose between headless-only or full-stack deployment
- **Professional Media**: Designed for news agencies and digital media organizations

For Russian documentation, visit [../ru/](../ru/).