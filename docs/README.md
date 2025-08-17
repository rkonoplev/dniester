# Documentation

## 📂 Configuration overview

This project uses **Spring Boot’s profile-based configuration** with multiple YAML files:

| File                          | Purpose |
|-------------------------------|---------|
| `application.yml`             | Base defaults (used if no specific profile is active). Should be minimal and never contain secrets. |
| `application-local.yml`       | Local developer configuration. Provides defaults for developers and reads credentials from `.env`. |
| `application-dev.yml`         | Development / Staging / CI servers configuration. Uses environment variables for DB connection and credentials. |
| `application-prod.yml`        | Production configuration. Strict mode: schema is only validated, no auto-updates, no SQL logs. All secrets must come from environment variables (injected by CI/CD or container orchestrator). |
| `application-test.yml`        | Test configuration. Uses an in-memory H2 database in MySQL compatibility mode for isolated, fast tests. |

### 🔐 Environment variables (`.env`)

- Local and dev environments read credentials and port settings from a `.env` file.
- Example:

```dotenv
MYSQL_DATABASE=newsdb
MYSQL_USER=newsuser
MYSQL_PASSWORD=newspass
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/newsdb?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
ADMIN_USERNAME=admin
ADMIN_PASSWORD=adminpass
```

## API Testing with Makefile

This project includes a `Makefile` with handy commands to test API endpoints using `curl`.

**Usage:**

1. Copy or rename the file `Makefile` (with a capital "M") into your project root.
2. Set your API credentials as environment variables, or override them on the command line:

```bash
export API_USER=yourusername
export API_PASS=yourpassword
make test-news-post
```
or

```bash
make test-news-post API_USER=yourusername API_PASS=yourpassword
```

## 🐳 Production Deployment with Docker Compose

For **production environments**, always run the application with the production override file:

```bash
docker compose -f docker-compose.yml -f docker-compose.override.yml up -d
```
##  🔐 Secrets Management
- All sensitive credentials (DB root password, DB user/pass, admin user/pass) must be injected through Docker Secrets.
- The override file (docker-compose.override.yml) is configured to use the *_FILE convention so Spring Boot and MySQL can read secrets directly from mounted files inside /run/secrets/....
- This means secrets are never stored in the codebase or .env files.

### Preparing secrets before deployment
Create a secrets/ directory locally (or provide via CI/CD pipeline):
```bash
mkdir -p secrets
echo "superRootPass" > secrets/mysql_root_password.txt
echo "newsuser" > secrets/db_user.txt
echo "secureDbPass" > secrets/db_password.txt
echo "admin" > secrets/admin_user.txt
echo "UltraSecure!" > secrets/admin_password.txt
```
###  ⚠️ Important:

Add the secrets/ folder to .gitignore to ensure secret values are never committed to Git.
In production, it is recommended to manage these secrets through your orchestration platform (e.g., Docker Swarm Secrets, Kubernetes Secrets, or CI/CD encrypted variables).
The SPRING_PROFILES_ACTIVE is set to prod inside the override file to automatically enable production configuration (application-prod.yml).


## 🆚 Development vs Production vs Test Configuration

| Aspect                | Development (Local / Dev)                           | Production (`docker-compose.override.yml`)                                   | Test (`application-test.yml`)                        |
|------------------------|----------------------------------------------------|------------------------------------------------------------------------------|-----------------------------------------------------|
| **Config files**       | `.env` + `application-local.yml` / `application-dev.yml` | `application-prod.yml` (activated via `SPRING_PROFILES_ACTIVE=prod`)         | `application-test.yml` (automatically when running tests) |
| **Secrets storage**    | `.env` file (dev-only, stored locally, in `.gitignore`) | **Docker Secrets / CI/CD variables** (never committed in repo)               | No secrets required, H2 in-memory DB auto-managed    |
| **DB exposure**        | MySQL port `3306` exposed to host (e.g., DBeaver)  | DB not exposed externally, only reachable inside Docker network              | In-memory H2 database, isolated from real DB         |
| **Schema management**  | `ddl-auto=update` (auto apply schema changes)      | `ddl-auto=validate` (strict schema validation, no auto changes)              | `ddl-auto=create-drop` (schema rebuilt for each run) |
| **SQL logging**        | Enabled (`show-sql: true`, DEBUG logging)          | Disabled (`show-sql: false`, only INFO/WARN/ERROR)                           | Optional: enabled for debugging during tests         |
| **Admin credentials**  | From `.env`, with defaults (e.g., `admin/adminpass`)| Injected securely via Docker Secrets / CI/CD pipeline variables              | Typically mocked or not required for automated tests |
| **Volumes**            | Local bind mount (`./db_data`)                     | Named Docker volumes (`db_data`, `app_logs`, persisted securely)             | No external volume (H2 runs in memory)              |
| **Startup**            | `docker compose up -d` with `.env`                 | `docker compose -f docker-compose.yml -f docker-compose.override.yml up -d`   | Tests run via `./gradlew test` or IDE runner         |



## Example API Requests (with curl)

Below are typical ways to interact with the News Platform API using `curl`.  
Replace `<USERNAME>` and `<PASSWORD>` with your account credentials.

---

### 1. Create a News Item

```bash
curl -u <USERNAME>:<PASSWORD> \
  -H "Content-Type: application/json" \
  -X POST http://localhost:8080/api/admin/news \
  -d '{"title":"My news title","content":"Some content here","category":"general"}'
```

### 2. Delete a News Item by ID
   ```bash
   curl -u <USERNAME>:<PASSWORD> \
   -X DELETE http://localhost:8080/api/admin/news/<NEWS_ID>
   Replace <NEWS_ID> with the numeric ID of the news item you want to delete.
   ```

### 3. Get All News Items
   ```bash
   curl -u <USERNAME>:<PASSWORD> \
   -X GET http://localhost:8080/api/news
   ```

### 4. Check Number of News Records
   To check how many news records you currently have, you can run:
   ```bash
   curl -u <USERNAME>:<PASSWORD> \
   -X GET http://localhost:8080/api/news
   ```
### Note:

These examples use http://localhost:8080 for local development.
Replace with your API server address as needed.
Never use real passwords or production credentials in public documentation or scripts.

