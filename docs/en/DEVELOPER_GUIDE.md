> [Back to Documentation Contents](./README.md)
>
> **[Quick Start Guide](./QUICK_START.md)**: Brief instructions for getting started quickly and
> performing daily tasks with an already configured project.

# Developer Guide – Local Workflow and CI/CD

This document explains how to work on the project locally (IntelliJ IDEA, Gradle, Docker)
and what will be automatically executed in GitHub Actions (CI/CD).

> For definitions of key terms and technologies, please refer to the **[Glossary](./GLOSSARY.md)**.

---

## Table of Contents
- [Initial Project Setup](#initial-project-setup)
- [Daily Local Workflow](#daily-local-workflow)
- [Setting Up Code Auto-formatter](#setting-up-code-auto-formatter)
- [Before Pushing to GitHub](#before-pushing-to-github)
- [What CI/CD Does (on GitHub Actions)](#what-cicd-does-on-github-actions)
- [Summary](#summary)
- [Database Migrations (Flyway)](#database-migrations-flyway)

---

## Initial Project Setup

For the first-time setup of the project, please refer to the **[Setup Guide](./SETUP_GUIDED.md)**.

---

## Daily Local Workflow

### Recommended Process (via Makefile)
The entire primary workflow is built around the `Makefile` for simplicity.

- **Run the full environment** (DB + backend):
  ```bash
  make run
  ```
- **Run all tests** (unit + integration):
  ```bash
  make all-tests
  ```
- **Stop the environment**:
  ```bash
  make stop
  ```
> For a complete list of commands, see **[Testing and Development with Makefile](./TESTING_WITH_MAKEFILE.md)**.

### Alternative/Low-Level Launch
The `make` commands are wrappers around `docker-compose` and `gradlew`. You can use them directly.

- **Run unit tests only** (does not require Docker):
  In IDEA or with the command:
  ```bash
  cd backend && ./gradlew test
  ```
- **Run integration tests** (requires Docker to be running):
  ```bash
  cd backend && ./gradlew integrationTest
  ```
- **Run the application manually**:
  1. Start the database: `docker-compose up -d phoebe-mysql`
  2. Run the application: `cd backend && ./gradlew bootRun`

---

## Setting Up Code Auto-formatter

The project uses a consistent Java code formatting style with a line length of **120 characters**.
The configuration is located in `.idea/codeStyles/` and is automatically applied when you open the project.

- **Check settings**: `File → Settings → Editor → Code Style → Scheme = "Project"`.
- **Format code**: `Ctrl+Alt+L` (Win/Linux) or `Cmd+Alt+L` (Mac).
- **Recommended**: Enable format on save: `Settings → Tools → Actions on Save → Reformat code`.

---

## Before Pushing to GitHub

**Recommended Check:**
```bash
make all-tests
```
This command emulates the full CI check.

**Minimum Manual Checks:**
- Code compiles (`./gradlew build`).
- All unit tests pass (`./gradlew test`).
- Integration tests pass (`./gradlew integrationTest`).

---

## What CI/CD Does (on GitHub Actions)

After every `push` or `pull request`, GitHub Actions will automatically perform:
- A full Gradle build (`clean build`).
- Execution of all tests (`integrationTest`, which includes `test`).
- Static code analysis: Checkstyle and PMD (as part of the `build` task).
- Test coverage reporting (JaCoCo) and upload to Codecov.

> **Note**: Secret scanning (GitLeaks) is no longer an automated step in CI and is recommended
> for local execution.

---

## Summary

- You do not need to keep Docker running for regular development (writing code, unit tests).
- It is recommended to run `make all-tests` before pushing to be confident.
- All heavy analysis and integration tests will be additionally verified in CI.

---

## Database Migrations (Flyway)

The project uses Flyway to manage database schema evolution. To support multiple database systems,
the migration scripts are organized into separate directories.

### Directory Structure
- `src/main/resources/db/migration/common/`: Common scripts for all databases.
- `src/main/resources/db/migration/mysql/`: Scripts specific to MySQL.
- `src/main/resources/db/migration/postgresql/`: Scripts specific to PostgreSQL.
