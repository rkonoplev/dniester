[![Java CI with Gradle](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml)

# News Platform

**Monorepo for a news publishing platform**  
✅ Backend: Spring Boot  
🛠 Frontend: Gatsby + JavaScript 
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

**Directory layout:**

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

🌐 Frontend Stack

Gatsby (React-based framework)
JavaScript
GraphQL (content querying)
CSS-in-JS / Responsive Design

Commands:
cd frontend
npm install
npm run develop

## 📌 Planned Features

- **MariaDB integration as the primary database**
- **Authentication & Authorization with Spring Security (JWT or OAuth2)**
- **CI/CD Pipeline with GitHub Actions**

📖 Documentation
Project documentation and technical requirements are stored in the [docs/](docs/) folder.

📜 License
MIT License. See [LICENSE](LICENSE) for details.


