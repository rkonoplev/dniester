# Setup and Deployment Guide

This guide is your main entry point for deploying the project. It describes all possible scenarios, from migrating an existing site to a clean installation.

> For daily work with an already configured project, please refer to the **[Quick Start Guide](./QUICK_START.md)**.

---

## Requirements

-   **JDK 21+**: Java Development Kit, version 21 or newer.
-   **Docker**: Required to run the environment.
-   **Git**: For cloning the repository.
-   **Make**: (Recommended) For executing commands from the `Makefile`.

---

## Initial Setup

Before choosing a scenario, perform these two steps:

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/rkonoplev/phoebe.git
    cd phoebe
    ```

2.  **Create the environment file**:
    ```bash
    cp .env.example .env
    ```
    In most cases, you will not need to modify the `.env` file.

### The `docker-compose.yml` File

The `docker-compose.yml` file in the project's root directory is crucial for local development,
orchestrating multiple services within Docker containers. It defines and launches:

-   **`phoebe-mysql`**: A container for the MySQL database.
-   **`phoebe-app`**: A container for the Spring Boot backend application.
-   **`nextjs-app`**: A container for the reference Next.js frontend application.

Using `docker-compose.yml` allows you to quickly bring up all necessary project components with a single command,
ensuring a consistent development environment.

---

## Choosing a Deployment Scenario

- **[Scenario 1: Migrating an Existing Drupal 6 Site](#scenario-1-migrating-an-existing-drupal-6-site)**
  - **Goal**: You have a database dump from a Drupal 6 site and want to migrate its data into Phoebe CMS.

- **[Scenario 2: Quick Start for a New Developer (Recommended)](#scenario-2-quick-start-for-a-new-developer-recommended)**
  - **Goal**: To get the project running locally as quickly as possible with a full, ready-to-use database that has already been migrated.

- **[Scenario 3: Clean Installation (New Site)](#scenario-3-clean-installation-new-site)**
  - **Goal**: To launch the project without historical data to start populating a brand-new site from scratch.

- **[Scenario 4: Backing Up and Transferring the Project](#scenario-4-backing-up-and-transferring-the-project)**
  - **Goal**: To save the current state of your local database for transfer to another machine.

- **[Scenario 5: Deploying to Production](#scenario-5-deploying-to-production)**
  - **Goal**: To deploy the application to a live server for public access.

---

### Scenario 1: Migrating an Existing Drupal 6 Site

This scenario is for those who have a Drupal 6 database dump and want to migrate it into the new system.

1.  **Perform the Data Transformation**: Follow the instructions in the **[Historical Migration Guide](./MIGRATION_DRUPAL6.md)** to convert your Drupal 6 dump into a final `clean_schema.sql` file.

2.  **Configure the Environment for MySQL**: Ensure that your `docker-compose.yml` and `.env` files are set up for MySQL.

3.  **Start the MySQL Container**:
    ```bash
    docker-compose up -d phoebe-mysql
    ```

4.  **Import the New Schema**:
    ```bash
    docker exec -i phoebe-mysql mysql -uroot -proot phoebe_db < /path/to/your/new/clean_schema.sql
    ```

5.  **Run the Application**:
    ```bash
    cd backend && ./gradlew bootRun
    ```
    The application will start up using the existing, populated database.

---

### Scenario 2: Quick Start for a New Developer (Recommended)

This process is fully automated and is the standard way to get started with the project.

1.  **Complete** the [Initial Project Setup](#initial-project-setup) steps.
2.  **Run everything with a single command:**
    ```bash
    make run
    ```
    *Alternative command (without Makefile):* `docker compose up --build`

**Result:** Flyway will automatically create the database and populate it with all data from the `V3__insert_sample_data.sql` script. This process is described in more detail in the **[Modern Migration Guide](./MODERN_MIGRATION_GUIDE.md)**.

---

### Scenario 3: Clean Installation (New Site)

This scenario is for starting from scratch.

1.  **Disable the data migration.**
    - Navigate to `backend/src/main/resources/db/migration/common/`.
    - Find the script responsible for inserting data (e.g., `V3__insert_sample_data.sql`).
    - **Rename** it by changing the prefix from `V` to `_V` (e.g., `_V3__insert_sample_data.sql`).

2.  **(Optional) Choose a database**:
    The project uses MySQL by default. To switch to PostgreSQL:
    - **In `docker-compose.yml`**: comment out the `phoebe-mysql` service and uncomment `phoebe-postgres`.
    - **In `.env`**: comment out the MySQL variables and uncomment the PostgreSQL ones.
    - **In `build.gradle`**: comment out the `mysql-connector-j` dependency and uncomment `postgresql`.

3.  **Run the project**:
    ```bash
    make run
    ```
    *Alternative launch (without Makefile):*
    1. `docker compose up -d phoebe-mysql` (or `phoebe-postgres`)
    2. `cd backend && ./gradlew bootRun`

**Result:** Flyway will create all tables but will skip the data migration. You will get a clean database.

---

### Scenario 4: Backing Up and Transferring the Project

This process is for creating full snapshots of your local database.

- All steps and commands are described in detail in the **[Docker Volume Migration Guide for MySQL Data](./VOLUME_MIGRATION_GUIDE.md)**.
- For historical reference on manual backup and recovery processes relevant during the early stages of the project, see the **[Historical Docker Data Backup and Recovery Guide (Drupal 6 Migration Context)](./LEGACY_DOCKER_DATA_RECOVERY_GUIDE_EN.md)**.

---

### Scenario 5: Deploying to Production

This scenario covers moving the application from local development to a live environment. It involves server setup, secret management, and using Docker for deployment.

All detailed instructions, environment requirements, and configuration examples are described in a separate guide:

- **➡️ [Production Deployment Guide](./PRODUCTION_GUIDE.md)**

---

## Running the Frontend Locally

For full project functionality, you need to run not only the backend (API) but also one of the reference frontends. The frontend will interact with the backend via its API, so it doesn't matter how the backend is launched (in Docker or directly) or which database is used (Dockerized MySQL/PostgreSQL or locally installed).

### 1. Ensure the Backend is Running

Before launching the frontend, make sure the backend is running and accessible at `http://localhost:8080`. You can start it using `make run` (as in Scenario 2) or `cd backend && ./gradlew bootRun`.

### 2. Launch the Chosen Frontend

The Phoebe CMS project provides two reference frontends: Angular and Next.js. Choose the one you want to work with.

#### For the Angular Frontend:

1.  Navigate to the Angular frontend directory:
    ```bash
    cd frontends/angular
    ```
2.  Install dependencies (if you haven't done so already):
    ```bash
    npm install
    ```
3.  Start the Angular application:
    ```bash
    npm start
    ```
    The application will be available at `http://localhost:4200` (or another address indicated in the console).

#### For the Next.js Frontend:

1.  Navigate to the Next.js frontend directory:
    ```bash
    cd frontends/nextjs
    ```
2.  Install dependencies (if you haven't done so already):
    ```bash
    npm install
    ```
3.  Start the Next.js application:
    ```bash
    npm run dev
    ```
    The application will be available at `http://localhost:3000` (or another address indicated in the console).
