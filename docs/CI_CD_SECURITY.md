# 🔄 CI/CD & Security Guide

This document describes the **continuous integration pipeline**, quality tools, and security practices used in the News Platform project.

---

## ⚡ CI/CD Pipeline (GitHub Actions)

The project uses **GitHub Actions** via the `gradle-ci.yml` workflow.

### Workflow Jobs

1. **Setup**
    - Installs JDK (Temurin, Java 21).
    - Caches Gradle dependencies.

2. **Build**
    - Runs `./gradlew build` with `ci` profile.
    - Springs uses `application-ci.yml` → H2 in-memory DB (fast and isolated).

3. **Test**
    - Runs unit and integration tests with coverage:
      ```bash
      ./gradlew test jacocoTestReport
      ```
    - Uploads **JUnit test reports** as artifacts.
    - Uploads **JaCoCo coverage reports** to Codecov.

4. **Security**
    - Runs **GitLeaks** to detect leaked secrets in the codebase.
    - Uploads report if scan fails.

5. **Static Analysis: Checkstyle & PMD**
    - **Checkstyle** — enforces Java code style and formatting rules.
    - **PMD** — detects common programming flaws, unused code, and code smells.
    - Both tools are automatically executed in the Gradle `check` task.
    - Reports (XML + HTML) are generated and can be uploaded in CI as artifacts.

---

## 📊 Tools Integrated in CI

- **JaCoCo** — test coverage (artifact + Codecov report).
- **Codecov** — coverage metrics integrated to PRs.
- **Checkstyle** — code style enforcement.
- **PMD** — static analysis to detect code smells and bad practices.
- **GitLeaks** — secret scanning to prevent accidental leaks.
- **Bucket4j** — rate limiting dependency for API protection.

---

## 🔒 Security Best Practices

1. **Authentication Architecture**
    - **Basic Auth** with environment-based credentials (no database storage)
    - **Role-based access**: ADMIN and EDITOR roles
    - **BCrypt password encoding** for secure credential storage
    - **Multi-user support** through environment variables
    - Credentials never stored in application code or database entities

2. **Authentication Usage**
    ```bash
    # Admin access
    curl -u admin:securepassword http://localhost:8080/api/admin/news
    
    # Editor access
    curl -u editor:editorpass http://localhost:8080/api/admin/news
    ```

3. **Environment Variables for Auth**
    ```bash
    ADMIN_USERNAME=admin
    ADMIN_PASSWORD=securepassword
    EDITOR_USERNAME=editor
    EDITOR_PASSWORD=editorpass
    ```

4. **Secrets in CI**
    - Use GitHub **Repository Secrets** (Settings → Secrets → Actions).
    - Examples: `DEV_DB_URL`, `DEV_DB_USER`, `DEV_DB_PASS`, `ADMIN_PASSWORD`.
    - Never commit `.env` with real secrets into repo.

5. **Profiles for CI**
    - Always run CI tests with `ci` profile (uses H2 in-memory DB).
    - Ensures builds are independent of external databases.

6. **Docker & Deploy Secrets**
    - Local dev → `.env` (ignored by git).
    - CI → GitHub Secrets.
    - Render production → environment variables or Secret Files.

7. **GitLeaks**
    - Prevents committing credentials, API keys, or tokens by mistake.
    - Run locally before commit:
      ```bash
      gitleaks detect --source .
      ```

8. **Rate Limiting**
    - IP-based rate limiting protects against API abuse.
    - Different limits for public (100/min) and admin (50/min) endpoints.
    - In-memory bucket storage (resets on application restart).

---

## ✅ Summary

- CI/CD pipeline is **fully automated**: build → test → quality → security.
- Code quality is controlled with **Checkstyle**, **PMD**, **JaCoCo**, and **Codecov**.
- Secrets are strictly managed through environment variables/secrets.
- Security scans (GitLeaks) protect repository against secret leaks.
- **Rate limiting** provides API protection against abuse and DoS attacks.

---