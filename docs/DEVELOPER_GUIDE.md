# Developer Guide – Local Workflow and CI/CD Expectations

This document explains how developers should work with the project locally
(IntelliJ IDEA, Gradle, Docker), and what checks will be automatically run in GitHub Actions (CI/CD).

---

## 🔹 Local Development Workflow

You do **not** need to keep Docker containers or databases running all the time during active development.  
Focus on the code and use Docker only when you want to test the full application.  
The heavy checks (static analysis, security scanning, code coverage, etc.) are performed in GitHub Actions.

### Daily workflow in IntelliJ IDEA:
- **Write code** and use `Build Project` (`Ctrl+F9`) to compile changes.
- **Run unit tests** frequently:
    - Right-click test class/method → `Run Test`.
    - Or run via Gradle:
      ```bash
      ./gradlew test
      ```
- **Check style/linting locally** (optional, prevents CI failures):
  ```bash
  ./gradlew checkstyleMain checkstyleTest
  ```
 ### Optional (when needed):
  If you want to **run the full Spring Boot application locally:**
  Start Docker (e.g., database containers).
  Run the service with:
  ```bash
  ./gradlew bootRun
  ```
  After testing, stop Docker to avoid unnecessary CPU/memory usage.
 
 ## 🔹 Before Pushing to GitHub
  Before committing and pushing, check at least:

✅ Code compiles (Build Project or ./gradlew build)
✅ All **tests pass** (./gradlew test)
✅ Code style checks pass (./gradlew checkstyleMain checkstyleTest)
(optional but strongly recommended)

That’s usually enough — **GitHub Actions CI** will run additional steps:

🟢 Full Gradle build + unit tests.  
🟢 Static analysis with Checkstyle and PMD.  
🟢 JaCoCo coverage report + Codecov upload.  
🟢 GitLeaks secrets scanning.  
🟢 Code scanning alerts integration in GitHub Security.

## 🔹 Summary
👉 Developers **can work without Docker** most of the time.
👉 Run **unit tests and build locally** before pushing.
👉 Let **CI/CD (GitHub Actions)** handle static analysis (Checkstyle + PMD), coverage (JaCoCo + Codecov), and security (GitLeaks).

This approach ensures fast, resource‑light local development, while CI validates everything in the cloud.

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
**1. Start MySQL 5.7 (for Drupal 6 dump):**

```bash
docker compose -f docker-compose.drupal.yml up -d
docker logs -f news-mysql-drupal6
```
**2. Export old schema and re-import into dniester:**

```bash
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot a264971_dniester > db_data/drupal6_fixed.sql
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/drupal6_fixed.sql
```
**3. Run migration scripts:**

```bash
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/migrate_from_drupal6_universal.sql
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/migrate_cck_fields.sql
```
**4. Export normalized schema:**

```bash
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot dniester > db_data/clean_schema.sql
```
**5. Start MySQL 8.0 (target):**

```bash
docker compose -f docker-compose.yml up -d mysql
docker logs news-mysql
```
If root doesn’t work, fix root password using --skip-grant-tables (see docs/MIGRATION_DRUPAL6_TO_NEWSPLATFORM.md).

**6. Import final schema into MySQL 8.0:**

```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
```
Verify:

```bash
docker exec -it news-mysql mysql -uroot -proot -e "USE dniester; SHOW TABLES;"
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```
## 💾 File/Folder Structure

| Directory/File                            | Description                                     |
|-------------------------------------------|-------------------------------------------------|
| `news-platform/`                          | Root project directory                          |
| ├── `.github/`                            | GitHub configurations (CI/CD workflows)         |
| ├── `.idea/`                              | IDE configuration files                         |
| ├── `backend/`                            | Spring Boot backend application                 |
| │   ├── `.gradle/`                        | Gradle system/cache directory                   |
| │   ├── `.idea/`                          | Backend-specific IDE configs                    |
| │   ├── `build/` *(ignored in VCS)*       | Build output (classes, jars, reports, tmp)      |
| │   ├── `config/`                         | Static analysis configs                         |
| │   │   ├── `checkstyle/checkstyle.xml`   | Checkstyle rules                                |
| │   │   ├── `pmd/ruleset.xml`             | PMD rules                                       |
| │   │   └── `spotbugs/excludeFilter.xml`  | SpotBugs exclude file (legacy)                  |
| │   ├── `gradle/wrapper/`                 | Gradle wrapper JAR + properties                 |
| │   ├── `src/main/java/`                  | Application source code (Java)                  |
| │   ├── `src/main/resources/`             | Configs (`application-*.yml`, static, templates)|
| │   ├── `src/test/java/`                  | Unit and integration tests                      |
| │   ├── `build.gradle`                    | Gradle build configuration                      |
| │   ├── `settings.gradle`                 | Gradle settings                                 |
| │   ├── `Dockerfile.dev`                  | Dockerfile for local development                |
| │   ├── `gradlew` / `gradlew.bat`         | Gradle wrapper scripts (Unix / Windows)         |
| ├── `db_data/`                            | Database migration files and clean dumps        |
| │   ├── `clean_schema.sql`                | Clean database schema                           |
| │   ├── `detect_custom_fields.sql`        | Custom fields detection                         |
| │   ├── `drupal6_fixed.sql`               | Fixed Drupal6 dump                              |
| │   ├── `migrate_cck_fields.sql`          | CCK fields migration                            |
| │   └── `migrate_from_drupal6_universal.sql` | Universal migration SQL script                |
| ├── `db_dumps/`                           | Original (raw) database dumps                   |
| ├── `docs/`                               | Documentation                                   |
| │   ├── `ARCHITECTURE_MIGRATION.md`       | Migration architecture guide                    |
| │   ├── `CI_CD_SECURITY.md`               | CI/CD & security guide                          |
| │   ├── `CONFIG_GUIDE.md`                 | Configuration guide                             |
| │   ├── `DEVELOPER_GUIDE.md`              | Developer guide (EN)                            |
| │   ├── `DEVELOPER_GUIDE_RU.txt`          | Developer guide (RU, text only)                 |
| │   ├── `DOCKER_GUIDE.md`                 | Docker setup guide                              |
| │   ├── `MIGRATION_DRUPAL6.md`            | Full migration guide (EN)                       |
| │   ├── `MIGRATION_DRUPAL6_RU.txt`        | Migration guide (RU)                            |
| │   ├── `README.md`                       | Docs index page                                 |
| │   └── `TECHNICAL_SPEC.md`               | Technical specification                         |
| ├── `frontend/`                           | Placeholder for future frontend app             |
| ├── `.env.dev`                            | Local development environment variables         |
| ├── `.env.prod`                           | Production environment variables                |
| ├── `.gitignore`                          | Git ignore rules                                |
| ├── `.gitleaks.toml`                      | GitLeaks secret-scanning config                 |
| ├── `codecov.yml`                         | Codecov configuration                           |
| ├── `create_baseline.sh`                  | Baseline creation script                        |
| ├── `docker-compose.yml`                  | Main Docker Compose file                        |
| ├── `docker-compose.override.yml`         | Override Compose file (prod overrides)          |
| ├── `docker-compose.drupal.yml`           | Compose setup for Drupal6 migration             |
| ├── `Dockerfile`                          | Production Dockerfile                           |
| ├── `LICENSE`                             | Project license                                 |
| ├── `Makefile`                            | Common make commands                            |
| └── `README.md`                           | Main repository README                          |


## ✅ TL;DR Commands
```bash
# 1. Start MySQL 8.0
docker compose -f docker-compose.yml up -d mysql
docker logs news-mysql

# 2. If root auth issue → reset password manually via skip-grant-tables
# (See full doc under docs/MIGRATION_DRUPAL6.md)

# 3. Import normalized schema
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql

# 4. Verify that schema and data are present
docker exec -it news-mysql mysql -uroot -proot -e "USE dniester; SHOW TABLES;"
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```
👉 For full migration walkthrough, see MIGRATION_DRUPAL6_TO_NEWSPLATFORM.md.


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

- **Checkstyle** — style guide and formatting enforcement, runs in CI
- **PMD** — detects code smells and common programming issues, runs in CI
- **CodeCov** — publishes code coverage reports to GitHub PRs (via JaCoCo)

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
## 🚀 Running the Project

After migrating the database and producing `clean_schema.sql`, you can run the full News Platform stack (MySQL + Spring Boot) or only the database for verification.

### 🟢 Option A. Run only the database (MySQL check)

```bash
# Stop any previous containers and volumes
docker compose -f docker-compose.yml down -v

# Start MySQL 8.0 only
docker compose -f docker-compose.yml up -d mysql

# Check logs
docker logs -f news-mysql

# Import the clean schema (if not imported yet)
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql

# Verify content
docker exec -it news-mysql mysql -uroot -proot -e "USE dniester; SHOW TABLES;"
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```
### 🟢 Option B. Run the full stack (Spring Boot + MySQL)
**1. Ensure you have .env.dev in the project root with proper DB configs:**

```dotenv
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=dniester
SPRING_LOCAL_PORT=8080
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/dniester?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
```
**2. Start containers:**

```bash
docker compose --env-file .env.dev up -d
```
news-mysql → MySQL 8.0 with schema dniester
news-app → Spring Boot backend

**3. Import clean schema if needed:**

```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
```
**4. Verify DB data:**

```bash
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```
**5. Check application logs:**

```bash
docker logs -f news-app
```
**6. Open API:**

```text
http://localhost:8080
```
### ✅ Quick TL;DR
```bash
docker compose --env-file .env.dev up -d
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```
---

## 🔁 Daily Workflow

**Do not need to import** `clean_schema.sql` every time you restart your computer 🚫.

### Why?
- MySQL container stores all database data inside `/var/lib/mysql`.
- In `docker-compose.yml` we mounted a persistent Docker volume:
  ```yaml
  volumes:
    - mysql_data:/var/lib/mysql
  ```
This volume (news-platform_mysql_data) survives container restarts and system reboots.

### Rules
✅ Use docker compose up -d every morning → your data is still there.
❌ Do NOT run docker compose down -v unless you want to wipe all data and re-import.

### What should be running?
**Option A (DB only):**

- Containers: news-mysql
- Volumes: news-platform_mysql_data

**Option B (DB + backend):**

- Containers: news-mysql + news-app
- Volumes: news-platform_mysql_data

👉 The old migration container news-mysql-drupal6 and its volume news-platform_mysql_data_drupal6 can be safely removed.

**Daily Start Command**
```bash
docker compose --env-file .env.dev up -d
```
Check:

```bash
docker ps
docker volume ls | grep news-platform
```
Expected:

- Containers: news-mysql (+ news-app)
- Volumes: news-platform_mysql_data

---

## 🛠️ MySQL Handy Commands Cheat Sheet

### 1. Connect to MySQL container (interactive shell)

```bash
docker exec -it news-mysql mysql -uroot -proot
```
Now you are inside the MySQL CLI (mysql>):

List all databases:

```sql
SHOW DATABASES;
```
Select the active database:

```sql
USE dniester;
```
List all tables:

```sql
SHOW TABLES;
```
Example: check content count:

```sql
SELECT COUNT(*) FROM content;
```
Exit MySQL CLI:

```sql
EXIT;
```
**2. Dumping databases (export)**
From the terminal (outside MySQL):

Dump a single database:

```bash
docker exec -i news-mysql mysqldump -uroot -proot dniester > db_data/exported_dump.sql
```
Dump a specific table:

```bash
docker exec -i news-mysql mysqldump -uroot -proot dniester users > db_data/users_dump.sql
```
**3. Importing dumps**
Load an SQL file back into MySQL:

```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/exported_dump.sql
```
👉 Notes:

Replace dniester with the db name you want to use.
Make sure the database exists before importing.
