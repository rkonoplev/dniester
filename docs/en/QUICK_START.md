> For definitions of key terms and technologies, please refer to the **[Glossary](./GLOSSARY.md)**.

Quick instructions for developers for **daily work** with an already configured project.

> **Important**: For **initial project setup**, please follow the detailed
> [Setup Guide](./SETUP_GUIDE.md).

---

## Recommended Quick Start (via Makefile)

The entire primary workflow is built around the `Makefile` for simplicity. This is the easiest way to get started.

- **Run the project**: `make run`
- **Run all tests**: `make all-tests`
- **Stop the project**: `make stop`

> For a complete list of commands and descriptions, refer to **[Testing and Development with Makefile](./TESTING_WITH_MAKEFILE.md)**.

---

## What are Backend and Frontend?

When you run the project, two main services are started:

- **Backend** (`phoebe-app`, port `8080`)
  - This is a Spring Boot application that handles the API, logic, database, and authorization.
  - It is accessible at: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

- **Frontend** (`phoebe-nextjs`, port `3000`)
  - This is a React/Next.js application—a visual interface that communicates with the backend via the API.
  - It is accessible at: [http://localhost:3000](http://localhost:3000)

---

## Most Common Commands (Cheat Sheet)

| Goal | Command |
|:---|:---|
| 🚀 **Start the project in the morning** | `make run` |
| 🛑 **Stop it in the evening** | `make stop` |
| 📋 **View backend logs** | `docker compose logs -f phoebe-app` |
| 🧹 **Completely rebuild from scratch** | `make hard-rebuild` (or manually) |
| 💣 **Delete everything, including the database** | `make reset` |

---

## Important for Newcomers: How to Update Code in Docker?

When working with Docker, it's important to understand how your code changes are applied to the container.

- **Quick Update (with cache)**: `make run`
  This command uses `docker compose up --build`. It quickly rebuilds only the parts that have changed.
  Use this 99% of the time for daily development.

- **Full Rebuild (no cache)**: `make rebuild`
  This command uses `docker compose build --no-cache`. It completely rebuilds the image from scratch.
  Use this only if you have changed dependencies (`build.gradle`), the `Dockerfile` itself, or if the project is behaving strangely.

> For a detailed explanation of all commands and scenarios, see the **[Docker Guide](./DOCKER_GUIDE.md)**.

---

## Requirements
- JDK 21+
- Docker & Docker Compose
- Git

---

## Alternative Launch (directly via Docker and Gradle)

### 1. Start Development Environment

This command starts all necessary services (database and application) defined in `docker-compose.yml`.

```bash
docker compose up --build -d
```

### 2. Check Status

- **Check containers**: `docker ps` → should show `phoebe-app` and `phoebe-mysql`
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

### 3. Stop Environment

To stop all services and free up resources:
```bash
docker compose down
```

> **Note**: Using `docker compose down -v` will completely remove your database data.

---

## Main Development Commands

All commands are executed from the `backend/` directory.

### Run Tests
```bash
./gradlew clean test
```

### Build Project
```bash
./gradlew build
```

---

## Troubleshooting

### Resetting the Database
```bash
make reset
```
Or manually: `docker compose down -v && docker compose up -d`

### Port Conflicts
- **MySQL**: port `3306`
- **Spring Boot**: port `8080`
If these ports are in use on your machine, stop the conflicting services or change the ports in the `.env` file.

---

## Managing a Running Project

### What's happening in the terminal?
When you run `make run` or `docker compose up`, Docker attaches the logs of all containers to your terminal. This is useful for debugging.

### How to exit the terminal without stopping services?

| Action | What it does | Command |
|:---|:---|:---|
| 💤 **Detach, but leave everything running** | Containers continue to run in the background | Press `Ctrl + p`, then `Ctrl + q` |
| 🛑 **Stop everything and exit** | Containers are shut down | Press `Ctrl + C` |
| 🚪 **Force stop** | If the terminal is frozen, open a new one and run | `make stop` |

### How to start and end your workday?
- **In the morning**: Make sure Docker is running and execute `make run`. The project will start in the same state, with database data preserved.
- **In the evening**: Execute `make stop`. This will correctly stop the containers while preserving data.
- **Full reset**: If you want to wipe everything, including the database, use `make reset`.

---

## Advanced Build Scenarios

### `make` Command Cheat Sheet

| Goal | When to use | What it does |
|:---|:---|:---|
| `make run` | For normal development, after code changes | Rebuilds and starts the project, with cache |
| `make stop` | To stop the project | Stops and preserves data |
| `make rebuild` | After updating Dockerfile, Gradle, dependencies | Rebuilds without cache for a clean update |
| `make reset` | When you want to start over from scratch | A full "reset" of containers and volumes |
| `make boot` | For debugging the backend locally without Docker | Starts Spring Boot directly |
| `make test` / `make all-tests` | To run tests | Testcontainers / unit+integration |

### What does a "hard rebuild" do?
Sometimes, a simple `make rebuild` might not be enough. For those cases, there is a more radical method you can perform manually:

```bash
docker compose down       # 1. Stops and cleans up containers
docker compose build --no-cache phoebe-app  # 2. Fully rebuilds the backend without cache
docker compose up         # 3. Starts from scratch
```

**When is this really necessary?**
Use this method if you have:
- changed `Dockerfile.dev`;
- changed dependencies in `build.gradle`;
- changed the version of JDK, Gradle, or plugins;
- suspect that Docker is using a "dirty" cache and a normal `rebuild` is not helping.

**When is this NOT necessary?**
If you are only changing Java code, `.yml` configs, or the frontend, the lightweight `make run` command is sufficient.
