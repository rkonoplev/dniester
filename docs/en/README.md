# Documentation

This directory contains all English project documentation for **Phoebe CMS**.

> **Русская документация**: See [../ru/](../ru/) for Russian documentation.

---

## 📖 Core Documentation

- **[Project Overview](./PROJECT_OVERVIEW.md)**  
  A comprehensive overview of the project, its architecture, technology stack, and goals.

- **[Glossary](./GLOSSARY.md)**  
  Definitions of key terms and technologies used throughout the documentation.

- **[Technical Debt](./TECHNICAL_DEBT.md)**  
  A list of known issues, planned improvements, and future work for the project.

- **[Setup Guide](./SETUP_GUIDE.md)**  
  Step-by-step instructions for the initial project setup, covering both migration from
  Drupal and a clean installation with MySQL or PostgreSQL, including system requirements.

- **[Developer Guide](./DEVELOPER_GUIDE.md)**  
  The daily workflow for developers, local commands, CI/CD expectations, code formatting, and troubleshooting common issues.

- **[API Reference](./API_REFERENCE.md)**  
  Complete REST API documentation with `curl` examples for frontend integration.

## 🏗️ Architecture & Setup

- **[Configuration Guide](./CONFIG_GUIDE.md)**  
  The Spring profiles matrix, `application-*.yml` files, and secrets management.

- **[Docker Guide](./DOCKER_GUIDE.md)**  
  Local development with `docker-compose` and production build notes.

## 🔐 Security & Authentication

- **[Authentication Guide](./AUTHENTICATION_GUIDE.md)**  
  Security implementation for the headless API and authentication methods.

- **[Security & Roles](./SECURITY_ROLES.md)**  
  A guide for implementing ADMIN and EDITOR role restrictions.

- **[Rate Limiting Guide](./RATE_LIMITING.md)**  
  IP-based rate limiting implementation with Bucket4j.

## 🎨 Frontend Integration

- **[Frontend Specification](./FRONTEND_SPEC.md)**  
  Technical specification for the reference frontend.

- **[Admin Panel Specification](./ADMIN_PANEL_SPEC.md)**  
  Technical requirements for admin panel interfaces.

## 🗄️ Database & Migration

- **[Database Guide](./DATABASE_GUIDE.md)**  
  Complete database documentation, including schema, setup, and migration scripts.

- **[Migration Drupal6 → Phoebe CMS](./MIGRATION_DRUPAL6.md)**  
  A complete guide for migrating from a legacy Drupal 6 site, covering detailed steps, troubleshooting, and post-migration cleanup.

## 🛠️ Development Tools

- **[CI/CD Guide](./CI_CD_GUIDE.md)**  
  The GitHub Actions workflow, automated testing, and code quality tools.

- **[Code Style Setup](./CODE_STYLE_SETUP.md)**  
  A guide for automatic code formatting in IntelliJ IDEA.

- **[Input Validation Guide](./VALIDATION_GUIDE.md)**  
  A guide to content validation, including SafeHtml and XSS protection.

---

## 🌐 Headless CMS Benefits

This documentation supports **Phoebe's hybrid headless approach**:

- **API-First**: Complete REST API documentation for custom frontend development.
- **Reference Implementation**: Guides for using the provided Angular frontend.
- **Flexibility**: Choose between headless-only or full-stack deployment.
- **Professional Grade**: Enterprise-ready features for any organization.

For Russian documentation, visit [../ru/](../ru/).