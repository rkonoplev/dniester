> [Back to Documentation Contents](./README.md)
>
> **[Developer Guide](./DEVELOPER_GUIDE.md)**: A detailed description of the full development workflow
> for developers, including IDE setup, testing, and CI/CD integration.

> For definitions of key terms and technologies, please refer to the **[Glossary](./GLOSSARY.md)**.

# Quick Start Guide

## Contents

- [Recommended Quick Start (via Makefile)](#recommended-quick-start-via-makefile)
- [Architecture Overview: Backend and Frontend](#architecture-overview-backend-and-frontend)
- [Most Frequent Commands (Cheat Sheet)](#most-frequent-commands-cheat-sheet)
- [Managing a Running Project](#managing-a-running-project)
  - [What happens in the terminal when starting?](#what-happens-in-the-terminal-when-starting)
  - [How to exit the terminal without stopping services?](#how-to-exit-the-terminal-without-stopping-services)
  - [Start and end of the workday](#start-and-end-of-the-workday)
- [How to update code in Docker?](#how-to-update-code-in-docker)
- [Advanced Build Scenarios](#advanced-build-scenarios)
  - [Make commands cheat sheet](#make-commands-cheat-sheet)
  - [What does a "hard rebuild" do?](#what-does-a-hard-rebuild-do)
- [Direct Project Management via Docker Compose (without Makefile)](#direct-project-management-via-docker-compose-without-makefile)
  - [Equivalence of `make` and `docker compose` commands](#equivalence-of-make-and-docker-compose-commands)
  - [Starting the entire environment](#starting-the-entire-environment)
  - [Checking status](#checking-status)
  - [Stopping the entire environment](#stopping-the-entire-environment)
  - [Full environment reset (with data deletion)](#full-environment-reset-with-data-deletion)
- [Manual control and debugging of Docker containers](#manual-control-and-debugging-of-docker-containers)
  - [Starting and stopping individual services](#starting-and-stopping-individual-services)
  - [Rebuilding and restarting a single service](#rebuilding-and-restarting-a-single-service)
  - [Debugging scenario: "sick" `phoebe-app`](#debugging-scenario-sick-phoebe-app)
- [Main Development Commands](#main-development-commands)
  - [Running tests](#running-tests)
  - [Building the project](#building-the-project)
  - [Code quality check](#code-quality-check)
- [Troubleshooting](#troubleshooting)
  - [Tests do not run (MySQL conflict)](#tests-do-not-run-mysql-conflict)
  - [Database reset](#database-reset)
  - [Port issues](#port-issues)
- [Additional Documentation](#additional-documentation)

Brief instructions for developers for **daily work** with an already configured project.

> **Important**: For **initial project setup**, please follow the detailed
> [Setup Guide](./SETUP_GUIDEMD).

---

## Recommended Quick Start (via Makefile)

The entire primary workflow is built around the `Makefile` for simplicity. This is the easiest way to get started.

- **Run the project**: `make run`
- **Run all tests**: `make all-tests`
- **Stop the project**: `make stop`

> For a complete list of commands and descriptions, refer to
> **[Testing and Development with Makefile](./TESTING_WITH_MAKEFILE.md)**.

---

## Architecture Overview: Backend and Frontend

When you run the project, two main services are started:

- **Backend** (`phoebe-app`, port `8080`)
  - This is a Spring Boot application that handles the API, logic, database, and authorization.
  - It is accessible at: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

- **Frontend** (`phoebe-nextjs`, port `3000`)
  - This is a reference frontend application (e.g., Next.js or Angular)—a visual interface that
    communicates with the backend via the API.
  - It is accessible at: [http://localhost:3000](http://localhost:3000)
  - Login credentials (usernames and passwords) for different roles are described in the file
    **[DATABASE_GUIDE.md](./DATABASE_GUIDE.md)**.

---

## Most Frequent Commands (Cheat Sheet)

| Goal | Command |
|:---|:---|
| Start the project in the morning | `make run` |
| Stop in the evening | `make stop` |
| View backend logs | `docker compose logs -f phoebe-app` |
| Completely rebuild from scratch | `make hard-rebuild` (or manually) |
| Delete everything, including the database | `make reset` |

---

## Managing a Running Project

### What happens in the terminal when starting?
When you run a command, for example:

```bash
make run
```
or
```bash
docker compose up
```
— Docker Compose starts all containers and "attaches" their logs directly to your terminal.
You will see the running logs of `phoebe-app`, `phoebe-mysql`, `phoebe-nextjs`, and so on.

This is normal and useful during debugging — you can immediately see if something went wrong.

### How to exit the terminal without stopping services?
There are three options:

| Action | What it does | Command |
|:---|:---|:---|
| Detach, but leave everything running | Containers continue to run in the background | Press `Ctrl + p`, then `Ctrl + q` |
| Stop everything and exit | Containers are shut down | Press `Ctrl + C` |
| Force stop Docker Compose | Same as above, but guaranteed | If the terminal is "frozen", type `make stop` in a new window |

### Start and end of the workday
- **In the morning**: Make sure Docker Desktop (or Docker Engine) is running.
  Navigate to the project folder and start the project:
  ```bash
  cd /path/to/your/project
  make run
  ```
  or, if you already had a build — it will be even faster to just:
  ```bash
  docker compose up
  ```
  Everything will start in the same state, the database will be preserved, as the `mysql_data` volume is not deleted.

- **In the evening**: Execute `make stop`. This will correctly stop the containers, preserving data.
- **Full reset**: If you want to wipe everything, including the database, use `make reset`.

---

## How to update code in Docker?

When working with Docker, it's important to understand how your code changes are applied to the container.

- **Quick Update (with cache)**: `make run`
  This command uses `docker compose up --build`. It quickly rebuilds only the parts that have changed.
  Use it 99% of the time for daily development.

- **Full Rebuild (no cache)**: `make rebuild`
  This command uses `docker compose build --no-cache`. It completely rebuilds the image from scratch.
  Use it only if you have changed dependencies (`build.gradle`), the `Dockerfile` itself,
  or if the project is behaving strangely.

> A detailed explanation of all commands and scenarios can be found in the **[Docker Guide](./DOCKER_GUIDE.md)**.

---

## Advanced Build Scenarios

### Make commands cheat sheet

| Goal | When to use | What it does |
|:---|:---|:---|
| `make run` | For normal development, after code changes | Rebuilds and starts the project, with cache |
| `make stop` | To stop | Stops and preserves data |
| `make rebuild` | After updating Dockerfile, Gradle, dependencies | Rebuilds without cache, to ensure update |
| `make reset` | When you want to wipe everything and start over | Full "reset" of containers and volumes |
| `make boot` | For debugging the backend locally without Docker | Starts Spring Boot directly |
| `make test` / `make all-tests` | To run tests | Testcontainers / unit+integration |

### What does a "hard rebuild" do?
Sometimes a simple `make rebuild` might not be enough. For this, there is a more radical method,
which can be performed manually:

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
- suspect that Docker is using a "dirty" cache, and a normal `rebuild` is not helping.

**When is this NOT necessary?**
If you are only changing Java code, `.yml` configs, or the frontend,
the lightweight `make run` command is sufficient.

---

## Direct Project Management via Docker Compose (without Makefile)

For developers who prefer or need to use `docker compose` commands directly,
this section provides equivalents to the main `make` commands.
More detailed information on working with Docker Compose can be found in the **[Docker Guide](./DOCKER_GUIDE.md)**.

### Equivalence of `make` and `docker compose` commands

| `make` command | `docker compose` equivalent | Description |
|:---------------|:-----------------------------|:---------|
| `make run`     | `docker compose up --build`  | Starts all services (DB, backend, frontend), rebuilding images with cache. |
| `make stop`    | `docker compose down`        | Stops all services, preserving database data. |
| `make reset`   | `docker compose down -v`     | Stops all services and completely removes volumes (including DB data). |

### Starting the entire environment

To start all project services (database, backend, and frontend) without using `Makefile`:

```bash
docker compose up --build
```
This command will build (using cache) and start all services defined in `docker-compose.yml`.

### Checking status

After starting, you can check the status of containers and service availability:

-   **Check running containers**:
    ```bash
    docker ps
    ```
-   **Backend API**: [http://localhost:8080](http://localhost:8080)
-   **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
-   **Frontend**: [http://localhost:3000](http://localhost:3000)
-   **Admin login credentials**: `admin` / `admin`

### Stopping the entire environment

To stop all running services, while preserving database data:

```bash
docker compose down
```

### Full environment reset (with data deletion)

If you want to completely clear all containers, networks, and **delete database data** (e.g., to start with a clean slate):

```bash
docker compose down -v
```
**Warning**: This command will irreversibly delete all data from your database.

---

## Manual control and debugging of Docker containers

Although `Makefile` provides convenient commands for most tasks, sometimes finer
control over individual Docker services is required. This is especially relevant during debugging, when you need
to restart or rebuild only one component without affecting others.

### Starting and stopping individual services

You can manage each service defined in `docker-compose.yml` individually:

-   **Start a specific service (and its dependencies)**:
    ```bash
    docker compose up -d <service_name>
    ```
    For example, to start only the database: `docker compose up -d phoebe-mysql`

-   **Stop a specific service**:
    ```bash
    docker compose stop <service_name>
    ```
    For example, to stop only the backend: `docker compose stop phoebe-app`

-   **Restart a specific service**:
    ```bash
    docker compose restart <service_name>
    ```
    For example: `docker compose restart phoebe-app`

### Rebuilding and restarting a single service

If you have made changes to the code or Dockerfile of a specific service and want to rebuild it,
without affecting others, use the following command:

```bash
docker compose up --build -d <service_name>
```
This command is "smart": it will rebuild the image only for the specified service (if there were changes),
restart it and all services that depend on it, while not touching other already running
services (e.g., the database).

### Debugging scenario: "sick" `phoebe-app`

Imagine you are working on the backend, and after changes, `phoebe-app` stopped responding
or "crashed", but `phoebe-mysql` and `phoebe-nextjs` continue to work.

1.  **Check `phoebe-app` logs**:
    ```bash
    docker compose logs -f phoebe-app
    ```
    This will help understand the cause of the failure.

2.  **Make corrections to the backend code**.

3.  **Rebuild and restart only `phoebe-app`**:
    ```bash
    docker compose up --build -d phoebe-app
    ```
    This command will:
    *   Rebuild the `phoebe-app` Docker image (using cache if possible, or without it,
        if you changed `Dockerfile.dev` or dependencies).
    *   Stop the old `phoebe-app` container.
    *   Start a new `phoebe-app` container.
    *   Restart `phoebe-nextjs`, as it depends on `phoebe-app`.
    *   **Will not touch `phoebe-mysql`**, if it is already running and healthy.

    This is significantly faster than a full `make hard-rebuild`, as it does not stop and
    rebuild all components.

---

## Main Development Commands

All commands are executed from the `backend/` directory.

### Running tests
```bash
./gradlew clean test
```

### Building the project
```bash
./gradlew build
```

### Code quality check
```bash
./gradlew checkstyleMain checkstyleTest
```

---

## Troubleshooting

### Tests do not run (MySQL conflict)
- Check that `spring.profiles.active: local` is **not** present in `application.yml`
- Flyway should be disabled in `application-test.yml`
- Run tests with a clean build: `./gradlew clean test`

### Database reset
```bash
docker compose down -v  # deletes all data
docker compose up -d    # creates a clean database
```

### Port issues
- MySQL: port 3306 (may conflict with local MySQL)
- Spring Boot: port 8080
- Stop local services or change ports in `.env.dev`

---

## Additional Documentation

- **[Setup Guide](./SETUP_GUIDE.md)**: Step-by-step instructions for the first launch.
- **[Developer Guide](./DEVELOPER_GUIDE.md)**: Detailed IDE setup and workflow description.
- **[Project Overview](./PROJECT_OVERVIEW.md)**: Full information about architecture and technologies.
- **[Migration from Drupal 6 (EN)](MIGRATION_DRUPAL6.md)**: Migration process.
- **[Dockerfile Optimization Guide](./DOCKERFILE_OPTIMIZATION_GUIDE.md)**: Recommendations for writing
  Dockerfiles for development and production.
