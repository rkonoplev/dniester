[![Java CI with Gradle](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml)
[![codecov](https://codecov.io/gh/rkonoplev/news-platform/graph/badge.svg?token=YOUR_TOKEN)](https://codecov.io/gh/rkonoplev/news-platform)
![GitHub](https://img.shields.io/github/license/rkonoplev/news-platform)

# News Platform

**Monorepo for a news publishing platform**  
✅ Backend: Spring Boot
🛠 Frontend: Gatsby + JavaScript
📚 Docs: Coming soon

## 🛠 Technology Stack
- Java 21
- Spring Boot
- Gradle
- MariaDB
- Spring Security
- React (frontend)

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

**Backend structure:**

- **controller** — REST controllers
- **service** — Business logic
- **repository** — Data access layer (database interaction)
- **dto** — Data Transfer Objects
- **mapper** — Entity mappers (conversion between entities and DTOs)
- **model** — JPA entities

**Requirements:**
- JDK 21+
- Gradle (or use `./gradlew` wrapper)

**Run:**
```bash
cd backend
./gradlew bootRun
```
🌐 Access: http://localhost:8080

## Code Quality Tools

This project uses several code quality tools to ensure code consistency and prevent bugs:

### Local Analysis Tools
- **Checkstyle** - Code style verification (runs locally during build)
    - Configuration: `config/checkstyle/checkstyle.xml`
    - Run: `./gradlew checkstyleMain checkstyleTest`

- **JaCoCo** - Test coverage reporting
    - Run: `./gradlew test` (coverage report generated automatically)
    - Reports: `build/reports/jacoco/test/html/index.html`

### Cloud Analysis Tools
- **Qodana** - Comprehensive code quality analysis in cloud
    - Automated CI/CD integration
    - Detailed reports available in JetBrains Qodana Cloud

- **CodeCov** - Code coverage analysis and reporting
    - Tracks test coverage percentage over time
    - Integrated with GitHub Actions for automated reporting
    - Detailed coverage reports available in CodeCov Cloud

### Security Tools
- **GitLeaks** - Secret detection (pre-commit hook / CI)
    - Run: `gitleaks detect --source .`

## 🚀 Development Commands

### Run all checks (tests + coverage + code style)
./gradlew check

### Run only code style checks
./gradlew checkstyleMain checkstyleTest

### Run tests with coverage
./gradlew test

### Check for secrets
gitleaks detect --source .

## 📌 Planned Features

- **MariaDB integration as the primary database**
- **Authentication & Authorization with Spring Security (JWT or OAuth2)**
- **CI/CD Pipeline with GitHub Actions**

## 🌐 Frontend Stack

Gatsby (React-based framework)
JavaScript
GraphQL (content querying)
CSS-in-JS / Responsive Design

Commands:
cd frontend
npm install
npm run develop

### Configuration
Create `application.properties` file in `backend/src/main/resources/` directory:
```bash
cp backend/src/main/resources/application.properties.template backend/src/main/resources/application.properties
```

## 📖 Documentation
Project documentation and technical requirements are stored in the [docs/](docs/) folder.

## 📜 License
MIT License. See [LICENSE](LICENSE) for details.
