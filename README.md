# News Platform

[![Java CI with Gradle](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/rkonoplev/news-platform/actions/workflows/gradle-ci.yml)
[![codecov](https://codecov.io/gh/rkonoplev/news-platform/graph/badge.svg?token=YOUR_TOKEN)](https://codecov.io/gh/rkonoplev/news-platform)
![GitHub](https://img.shields.io/github/license/rkonoplev/news-platform)

Monorepo for a modern news publishing platform.

Back end: Spring Boot  
Front end: GatsbyJS (React)  
Database: MySQL

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

## ⚙️ Development Environment

Start local dev environment (app + MySQL 8.0):

```bash
docker compose --env-file .env.dev up -d
```
Check running containers:

```bash
docker ps --format "table {{.Names}}\t{{.Image}}\t{{.Ports}}\t{{.Status}}"
```
Connect to database:

```bash
docker exec -it news-mysql mysql -uroot -proot dniester
```
Stop all services:

```bash
docker compose down
```

## 📦 Production Environment
Production setup uses docker-compose.override.yml and secure secrets.

Start with prod config:

```bash
docker compose \
  -f docker-compose.yml \
  -f docker-compose.override.yml \
  --env-file .env.prod \
  up -d
  ```
⚠️ .env.prod must NOT be committed — it should be provided via CI/CD secrets or Docker Secrets.

## 📚 Migration from Drupal 6
### Summary
Drupal 6 dump (drupal6_working.sql) is imported into a temporary MySQL 5.7 container.
Then data is normalized into clean schema with migration SQL scripts.
Finally, the normalized dump clean_schema.sql is loaded into MySQL 8.0 for News Platform.

### Migration Flow
1. Start MySQL 5.7 (for Drupal 6 dump):

```bash
docker compose -f docker-compose.drupal.yml up -d
docker logs -f news-mysql-drupal6
```
2. Export old schema and re-import into dniester:

```bash
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot a264971_dniester > db_data/drupal6_fixed.sql
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/drupal6_fixed.sql
```
3. Run migration scripts:

```bash
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/migrate_from_drupal6_universal.sql
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/migrate_cck_fields.sql
```
4. Export normalized schema:

```bash
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot dniester > db_data/clean_schema.sql
```
5. Start MySQL 8.0 (target):

```bash
docker compose -f docker-compose.yml up -d mysql
docker logs news-mysql
```
If root doesn’t work, fix root password using --skip-grant-tables (see docs/MIGRATION_DRUPAL6_TO_NEWSPLATFORM.md).

6. Import final schema into MySQL 8.0:

```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
```
Verify:

```bash
docker exec -it news-mysql mysql -uroot -proot -e "USE dniester; SHOW TABLES;"
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```
## 💾 File/Folder Structure

| Directory/File                     | Description                                  |
|------------------------------------|----------------------------------------------|
| `news-platform/`                   | Root project directory                      |
| `├── .github/`                     | GitHub configurations                       |
| `├── .idea/`                       | IDE configuration files                     |
| `├── backend/`                     | Spring Boot application                     |
| `│   ├── .gradle/`                 | Gradle cache directory                      |
| `│   ├── .idea/`                   | Backend-specific IDE configs                |
| `│   ├── build/`                   | Build output directory                      |
| `│   ├── config/`                  | Configuration files                         |
| `│   ├── gradle/`                  | Gradle wrapper files                        |
| `│   ├── src/`                     | Application source code                     |
| `│   ├── build.gradle`             | Gradle build configuration                  |
| `│   ├── Dockerfile.dev`           | Development Docker configuration            |
| `│   ├── gradlew`                  | Gradle wrapper (Unix)                       |
| `│   ├── gradlew.bat`              | Gradle wrapper (Windows)                    |
| `│   └── settings.gradle`          | Gradle project settings                     |
| `├── db_data/`                     | Database migration files and clean dumps    |
| `│   ├── clean_schema.sql`         | Clean database schema                       |
| `│   ├── detect_custom_fields.sql` | Custom fields detection script              |
| `│   ├── drupal6_fixed.sql`        | Fixed Drupal6 database dump                 |
| `│   ├── migrate_cck_fields.sql`   | CCK fields migration script                 |
| `│   └── migrate_from_drupal6_universal.sql` | Universal migration script          |
| `├── db_dumps/`                    | Original database dumps                     |
| `├── docs/`                        | Project documentation                       |
| `│   ├── ARCHITECTURE_MIGRATION.md`| Migration architecture docs                 |
| `│   ├── CLCD_SECURITY.md`         | Security documentation                      |
| `│   ├── CONFIG_GUIDE.md`          | Configuration guide                         |
| `│   ├── DOCKER_GUIDE.md`          | Docker setup guide                          |
| `│   ├── MIGRATION_DRUPAL6_RU.txt` | Russian migration notes                     |
| `│   ├── MIGRATION_DRUPAL6_TO_NEWSPLA*` | Drupal6 migration doc                |
| `│   ├── TECHNICAL_SPEC.md`        | Technical specifications                    |
| `│   └── ...`                      | Other documentation files                   |
| `├── frontend/`                    | Frontend application (planned)              |
| `├── .env.dev`                     | Local development environment variables     |
| `├── .env.prod`                    | Production environment variables            |
| `├── .gitignore`                   | Git ignore rules                            |
| `├── .gitleaks.toml`               | Secrets detection configuration             |
| `├── codecov.yml`                  | Code coverage configuration                 |
| `├── create_baseline.sh`           | Baseline creation script                    |
| `├── docker-compose.yml`           | Main Docker compose configuration           |
| `├── docker-compose.drupal.yml`    | Drupal6 migration setup                     |
| `├── docker-compose.override.yml`  | Production override configuration           |
| `├── Dockerfile`                   | Production Docker configuration             |
| `├── LICENSE`                      | Project license                             |
| `├── Makefile`                     | Project make commands                       |
| `├── qodana.yaml`                  | Qodana static analysis configuration        |
| `└── README.md`                    | Main project documentation                  |

## ✅ TL;DR Commands
```bash
# 1. Start MySQL 8.0
docker compose -f docker-compose.yml up -d mysql
docker logs news-mysql

# 2. If root auth issue → reset password manually via skip-grant-tables
# (See full doc under docs/MIGRATION_DRUPAL6_TO_NEWSPLATFORM.md)

# 3. Import normalized schema
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql

# 4. Verify that schema and data are present
docker exec -it news-mysql mysql -uroot -proot -e "USE dniester; SHOW TABLES;"
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```
👉 For full migration walkthrough, see docs/MIGRATION_DRUPAL6_TO_NEWSPLATFORM.md.

## Backend Layer Structure

- `controller` — REST API controllers
- `service` — Business logic layer
- `repository` — JPA data access layer
- `dto` — Data Transfer Objects
- `mapper` — Entity ↔ DTO mapping
- `model` — JPA entities

---

## 🛡️ Code Quality & Security Tools

This project uses several tools for code and security assurance.

### Local Analysis

- **Checkstyle**
    - Config: `config/checkstyle/checkstyle.xml`
    - Run:
      ```bash
      ./gradlew checkstyleMain checkstyleTest
      ```

- **JaCoCo**
    - Run:
      ```bash
      ./gradlew test
      ```  
    - Report: `build/reports/jacoco/test/html/index.html`

### Cloud Analysis

- **Qodana** — cloud code quality, CI-integrated
- **CodeCov** — code coverage, GitHub Actions integration

### Security

- **GitLeaks** (secret scanning tool)  
  Run:
  ```bash
  gitleaks detect --source .

---

## 🧑‍💻 Common Development Commands

| Task                    | Command                                   |
|-------------------------|-------------------------------------------|
| Run all checks          | `./gradlew check`                         |
| Run code style checks   | `./gradlew checkstyleMain checkstyleTest` |
| Run tests with coverage | `./gradlew test`                          |
| Secret scan             | `gitleaks detect --source .`              |

---

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

- [Architecture Migration](docs/ARCHITECTURE_MIGRATION.md)
- [Configuration Guide](docs/CONFIG_GUIDE.md)
- [Docker Guide](docs/DOCKER_GUIDE.md)
- [CI/CD & Security](docs/CI_CD_SECURITY.md)
- [Technical Specification](docs/TECHNICAL_SPEC.md)
- [API Usage Guide](docs/API_USAGE.md)
- [Migration Drupal6 → News Platform (EN)](docs/MIGRATION_DRUPAL6_TO_NEWSPLATFORM.md)
- [Migration Drupal6 → News Platform (RU, plain text)](docs/MIGRATION_DRUPAL6_RU.txt)

## 📜 License
MIT License. See [LICENSE](LICENSE) for details.
