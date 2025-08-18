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

5. **Qodana (Static Analysis)**
    - Uses [JetBrains Qodana](https://www.jetbrains.com/qodana/) for static code quality checks.
    - Results available in GitHub UI and stored as artifact.

---

## 📊 Tools Integrated in CI

- **JaCoCo** — test coverage (artifact + Codecov report).
- **Codecov** — coverage metrics integrated to PRs.
- **Qodana** — static code analysis for JVM.
- **GitLeaks** — secret scanning to prevent accidental leaks.

---

## 🔒 Security Best Practices

1. **Secrets in CI**
    - Use GitHub **Repository Secrets** (Settings → Secrets → Actions).
    - Examples: `DEV_DB_URL`, `DEV_DB_USER`, `DEV_DB_PASS`.
    - Never commit `.env` with real secrets into repo.

2. **Profiles for CI**
    - Always run CI tests with `ci` profile (uses H2 in-memory DB).
    - Ensures builds are independent of external databases.

3. **Docker & Deploy Secrets**
    - Local dev → `.env` (ignored by git).
    - CI → GitHub Secrets.
    - Render production → environment variables or Secret Files.

4. **GitLeaks**
    - Prevents committing credentials, API keys, or tokens by mistake.
    - Run locally before commit:
      ```bash
      gitleaks detect --source .
      ```

---

## ✅ Summary

- CI/CD pipeline is **fully automated**: build → test → quality → security.
- Code quality is controlled with **Qodana** + **JaCoCo** + **Codecov**.
- Secrets are strictly managed through environment variables/secrets.
- Security scans (GitLeaks) protect repository against secret leaks.

---