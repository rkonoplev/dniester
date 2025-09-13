# News Platform

[![Java CI with Gradle](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml)
[![codecov](https://codecov.io/gh/rkonoplev/news-platform/graph/badge.svg?token=YOUR_TOKEN)](https://codecov.io/gh/rkonoplev/news-platform)
![GitHub](https://img.shields.io/github/license/rkonoplev/news-platform)

Monorepo for a modern news publishing platform.

Back end: Spring Boot  
Front end: Angular with Angular Universal  
Database: MySQL

## 📑 Table of Contents
- [📂 Project Structure](#-project-structure)
- [🚀 Backend Quick Start](#-backend-quick-start)
- [📌 Key Features](#-key-features)
- [🌐 Frontend Stack (Planned)](#-frontend-stack-planned)
- [📖 Documentation](#-documentation)
- [🤝 Contributing](#-contributing)
- [📜 License](#-license)

## 📂 Project Structure

| Directory              | Description                        |
|------------------------|------------------------------------|
| `news-platform/`       | Root directory of the project      |
| `├── backend/`         | Spring Boot app (Java 21)          |
| `│   ├── src/`         | Source code for the backend        |
| `├── frontend/`        | Angular app (planned)              |
| `│   ├── src/`         | Source code for the frontend       |
| `│   └── package.json` | Package configuration for frontend |
| `└── docs/`            | Documentation                      |


---

## 🚀 Backend Quick Start

**Requirements:**
- JDK 21+
- Docker & Docker Compose
- Gradle (or use `./gradlew` wrapper)

### 1. Start backend dependencies (MySQL) via Docker Compose:
```bash
docker compose up -d
```

### 2. Start the Spring Boot application (uses .env variables for DB connection):

```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=local'
```
By default, the application reads database connection settings and credentials from the .env file.
Each Spring Boot profile (local, dev, prod, test) has its own YAML configuration file (application-<profile>.yml).
In production, you should override the variables directly via the runtime environment instead of .env.

API entrypoint: http://localhost:8080


## 📌 Key Features

- **Database**: MySQL 8.0 as primary database with H2 for tests
- **Security**: Spring Security with Basic Auth (OAuth 2.0 + 2FA planned for ADMIN, EDITOR, USER roles)
- **API**: RESTful endpoints with OpenAPI/Swagger documentation
- **Pagination**: Term-based filtering with configurable page sizes
- **Content Management**: Full CRUD operations for news articles
- **Taxonomy**: Category and tag system with flexible filtering
- **Rate Limiting**: IP-based rate limiting with Bucket4j (100 req/min public, 50 req/min admin)
- **CI/CD**: GitHub Actions pipeline with automated testing

---

## 🌐 Frontend Stack (Planned)

- Angular with Angular Universal for Google News–inspired design  
- Responsive, mobile-first layout with clean grid system  
- Static SEO-friendly URLs for all articles (SSR enabled)  
- Structured data (JSON-LD) + OpenGraph metadata for search engines  
- Branding with custom color palette (dark blue, red, white) and typography  
- Planned enhancements: search, dark mode, push notifications

**Run Frontend (when implemented):**
```bash
cd frontend
npm install
npm start
```

## 📖 Documentation
Full developer and deployment documentation is available in the [docs/](docs/) folder:

- **[Complete Project Information](docs/TASK_DESCRIPTION.md)** - Comprehensive project overview and technical documentation
- **[Quick Start (Russian)](docs/QUICK_START_RU.md)** - Быстрый старт для русскоязычных разработчиков
- [Developer Guide](docs/DEVELOPER_GUIDE.md)
- [Developer Guide (RU)](docs/DEVELOPER_GUIDE_RU.md)
- [Architecture Migration](docs/ARCHITECTURE_MIGRATION.md)
- [Configuration Guide](docs/CONFIG_GUIDE.md)
- [Authentication Guide](docs/AUTHENTICATION_GUIDE.md)
- [Role Security Implementation](docs/ROLE_SECURITY_IMPLEMENTATION.md)
- [Docker Guide](docs/DOCKER_GUIDE.md)
- [CI/CD & Security](docs/CI_CD_SECURITY.md)
- [Technical Specification](docs/TECHNICAL_SPEC.md)
- [Frontend Specification](docs/FRONTEND_SPEC.md)
- [Admin Panel Specification](docs/ADMIN_PANEL_SPEC.md)
- [API Usage Guide](docs/API_USAGE.md)
- [Migration Drupal6 → News Platform (EN)](docs/MIGRATION_DRUPAL6.md)
- [Migration Drupal6 → News Platform (RU)](docs/MIGRATION_DRUPAL6_RU.md)
- [Database Schema](docs/DATABASE_SCHEMA.md)
- [Database Migration Scripts](db_data/README.md)
- [Rate Limiting Guide](docs/RATE_LIMITING.md)
---
## ⚙️ Environment Setup

This guide explains how to run News Platform locally using Docker.

### 1. Clone the repository
```bash
git clone https://github.com/rkonoplev/news-platform.git
cd news-platform
```
### 2. Prepare environment variables
   Copy the example environment file to .env.dev:

```bash
cp .env.dev.example .env.dev
```
Edit .env.dev to adjust MySQL root password, database name, or admin username/password if needed.
⚠️ Do not commit this file — it is excluded via .gitignore.

### 3. Start services with Docker Compose
   ```bash
   docker compose --env-file .env.dev up -d
   ```
   This will start:

* news-mysql (MySQL 8.0 with schema newsdb)
* news-app (Spring Boot backend)
### 4. Verify running containers
   ```bash
   docker ps
   ```
   Expected:

news-mysql → status "healthy"
news-app → Spring Boot logs with "Server is running!"
### 5. Access the services
   - **API root**: http://localhost:8080
   - **Swagger UI**: http://localhost:8080/swagger-ui/index.html
   - **API Examples**:
     - All news: `GET /api/public/news?size=10&sort=publicationDate,desc`
     - By category: `GET /api/public/news/term/5?size=15`
     - Multiple terms: `GET /api/public/news/terms?termIds=1,3,5&size=20`
   - **Rate Limiting**: Automatic IP-based limits (check `X-Rate-Limit-Remaining` header)
### 6. Stop services
   ```bash
   docker compose down
   ```
### 7. Reset volumes (wipe DB completely if needed)
   ```bash
   docker compose down -v
   ```

## 🤝 Contributing
Contributions, issues, and feature requests are welcome! But not yet now. :-)

- Check the [issues](../../issues) page to see current tasks or report a bug.
- Open a [Pull Request](../../pulls) to propose changes.

Before submitting PRs, please:
- Follow the project’s coding style and guidelines
- Run local checks:
  ```bash
  ./gradlew build test checkstyleMain checkstyleTest
  ```
  
## 📜 License
MIT License. See [LICENSE](LICENSE) for details.
