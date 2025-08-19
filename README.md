# News Platform

# News Platform

[![Java CI with Gradle](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml)
[![codecov](https://codecov.io/gh/rkonoplev/news-platform/graph/badge.svg?token=YOUR_TOKEN)](https://codecov.io/gh/rkonoplev/news-platform)
![GitHub](https://img.shields.io/github/license/rkonoplev/news-platform)

Monorepo for a modern news publishing platform.

Back end: Spring Boot  
Front end: GatsbyJS (React)  
Database: MySQL

## 📑 Table of Contents
- [📂 Project Structure](#-project-structure)
- [🚀 Backend Quick Start](#-backend-quick-start)
- [📌 Planned Features](#-planned-features)
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
| `├── frontend/`        | Gatsby app (planned)               |
| `│   ├── src/`         | Source code for the frontend       |
| `│   └── package.json` | Package configuration for frontend |
| `└── docs/`            | Future documentation               |


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


## 📌 Planned Features

- MySQL as the primary database
- Authentication & Authorization (Spring Security with JWT/OAuth2)
- CI/CD pipeline based on GitHub Actions

---

## 🌐 Frontend Stack (Planned)

- Gatsby (React-based framework)
- JavaScript, GraphQL
- CSS-in-JS, Responsive design

**Run Frontend (when implemented):**
```bash
cd frontend
npm install
npm run develop
```
### Configuration
Create `application.properties` file in `backend/src/main/resources/` directory:
```bash
cp backend/src/main/resources/application.properties.template backend/src/main/resources/application.properties
```
## 📖 Documentation
Full developer and deployment documentation is available in the [docs/](docs/) folder:

- [Developer Guide](docs/DEVELOPER_GUIDE.md)
- [Architecture Migration](docs/ARCHITECTURE_MIGRATION.md)
- [Configuration Guide](docs/CONFIG_GUIDE.md)
- [Docker Guide](docs/DOCKER_GUIDE.md)
- [CI/CD & Security](docs/CI_CD_SECURITY.md)
- [Technical Specification](docs/TECHNICAL_SPEC.md)
- [API Usage Guide](docs/API_USAGE.md)
- [Migration Drupal6 → News Platform (EN)](docs/MIGRATION_DRUPAL6.md)
- [Migration Drupal6 → News Platform (RU, plain text)](docs/MIGRATION_DRUPAL6_RU.txt)
---
## 🤝 Contributing
Contributions, issues, and feature requests are welcome!

- Check the [issues](../../issues) page to see current tasks or report a bug.
- Open a [Pull Request](../../pulls) to propose changes.

Before submitting PRs, please:
- Follow the project’s coding style and guidelines
- Run local checks:
  ```bash
  ./gradlew build test checkstyleMain checkstyleTest
  
## 📜 License
MIT License. See [LICENSE](LICENSE) for details.
