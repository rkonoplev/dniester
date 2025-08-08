[![Java CI with Gradle](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml)


# News Platform

**Monorepo for a news publishing platform**  
✅ Backend: Spring Boot (ready)  
🛠 Frontend: Gatsby + JavaScript (planned)  
📚 Docs: Coming soon

---

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

## 📂 Project Structure

- **controller** — REST controllers
- **service** — Business logic
- **repository** — Data access layer (database interaction)
- **dto** — Data Transfer Objects
- **mapper** — Entity mappers (conversion between entities and DTOs)
- **model** — JPA entities

---

## 📌 Planned Features

- **MariaDB integration** — Connect and configure MariaDB as the primary database
- **Authentication & Authorization** — Implement user security with Spring Security (JWT or OAuth2)
- **CI/CD Pipeline** — Automate testing and deployment using GitHub Actions

**Requirements:**
- JDK 21+
- Gradle (or use `./gradlew` wrapper)

**Run:**
```bash
cd backend
./gradlew bootRun
```
🌐 Access: http://localhost:8080

🌐 Frontend (Planned)
Stack:

Gatsby (React-based framework)
JavaScript
GraphQL (for content querying)
CSS-in-JS / Responsive Design

Planned commands:
cd frontend
npm install
npm run develop

📜 License
MIT License. See LICENSE for details.


