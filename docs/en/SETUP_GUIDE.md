# Setup and Deployment Guide

This guide is your main entry point for deploying the project. It describes all possible scenarios, from a quick
start for a new developer to migrating data from a new Drupal 6 site.

> For definitions of key terms and technologies, please refer to the **[Glossary](./GLOSSARY.md)**.

## Requirements

To successfully set up and run the Phoebe CMS project, you will need the following software installed on your system:

-   **JDK 21+**: The Java Development Kit, version 21 or newer.
-   **Docker & Docker Compose**: These tools are essential for managing the local development environment.
-   **Git**: For cloning the project repository.

---

## Initial Project Setup

Before proceeding with any scenario, you need to clone the repository and set up your environment variables.

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/rkonoplev/phoebe.git
    cd phoebe
    ```

2.  **Configure Environment Variables**:
    Copy the example environment file to create your local development environment configuration:
    ```bash
    cp .env.dev.example .env.dev
    ```
    You will then edit `.env.dev` to match your specific database and application settings as described in the scenarios below.

---

## Choosing a Deployment Scenario

Choose the scenario that matches your goal and proceed to the detailed instructions below.

- **[Scenario A: Quick Start for a New Developer (Recommended)](#scenario-a-quick-start-for-a-new-developer-recommended)**
  - **Goal:** To get the project running locally as quickly as possible with a full, ready-to-use database.

- **[Scenario B: Clean Installation (New Site)](#scenario-b-clean-installation-new-site)**
  - **Goal:** To launch the project without historical data, to start populating a brand-new site.

- **[Scenario C: Migrating a NEW Drupal 6 Site](#scenario-c-migrating-a-new-drupal-6-site)**
  - **Goal:** You have a dump from a **different** Drupal 6 site, and you want to migrate its data.

- **[Scenario D: Backing Up and Restoring Current Work](#scenario-d-backing-up-and-restoring-current-work)**
  - **Goal:** To save the current state of your local database for transfer or as a precaution.

---

## Detailed Scenario Instructions

### Scenario A: Quick Start for a New Developer (Recommended)

This process is fully automated and is the standard way to get started with the project.

1.  **Complete** the [Initial Project Setup](#initial-project-setup) steps.
2.  **Run everything** with a single command: `docker compose up --build`.

**Result:** Flyway will automatically create the database and populate it with all data from the `V3__insert_sample_data.sql` script.
The process is described in more detail in the **[Modern Migration Guide](./MODERN_MIGRATION_GUIDE.md)**.

---

### Scenario B: Clean Installation (New Site)

This scenario is for starting from scratch. You can choose any of the supported databases.

1.  **Disable the data migration.**
    - Navigate to `backend/src/main/resources/db/migration/common/`.
    - Find the script responsible for inserting data (e.g., `V3__insert_sample_data.sql`).
    - **Rename** it by changing the prefix from `V` to `_V` (e.g., `_V3__insert_sample_data.sql`).
      This will cause Flyway to ignore it.

2.  **Configure Docker and environment variables** for your chosen database (MySQL or PostgreSQL) as described below.

3.  **Run the application** with the corresponding profile (`mysql` or `postgresql`).

#### Step 1: Configure Docker Compose

Open your `docker-compose.yml` file and ensure only one database service is active:

- **For MySQL**:
  ```yaml
  phoebe-mysql:
    image: mysql:8.0
    # ... rest of the configuration
  ```

- **For PostgreSQL**:
  ```yaml
  phoebe-postgres:
    image: postgres:13
    # ... rest of the configuration
  ```

#### Step 2: Configure Environment Variables (`.env.dev`)

- **For MySQL**:
  ```dotenv
  SPRING_DATASOURCE_URL=jdbc:mysql://phoebe-mysql:3306/phoebe_db?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
  SPRING_DATASOURCE_USERNAME=root
  SPRING_DATASOURCE_PASSWORD=root
  ```

- **For PostgreSQL**:
  ```dotenv
  SPRING_DATASOURCE_URL=jdbc:postgresql://phoebe-postgres:5432/phoebe_db
  SPRING_DATASOURCE_USERNAME=user
  SPRING_DATASOURCE_PASSWORD=password
  ```

#### Step 3: Run the Application with the Correct Profile

1.  **Start the Docker container** with your chosen database:
    ```bash
    docker compose up -d phoebe-mysql # or phoebe-postgres
    ```

2.  **Run the application**, specifying the corresponding database profile:
    ```bash
    cd backend
    ./gradlew bootRun --args='--spring.profiles.active=local,mysql' # or postgresql
    ```

**Result:** Flyway will create all tables but will skip the data migration. You will get a clean database.

---

### Scenario C: Migrating a NEW Drupal 6 Site

This process requires repeating the historical steps to convert your new dump into a Flyway script.

#### Step 1: Perform the Data Transformation

Follow the instructions in the [Historical Migration Guide](./MIGRATION_DRUPAL6.md) to convert your new
Drupal 6 dump into a final `clean_schema.sql` file.

#### Step 2: Configure the Environment for MySQL

1.  **Docker Compose**: Ensure the `mysql` service is active in your `docker-compose.yml` file.

2.  **Environment Variables**: In your `.env.dev` file, specify your MySQL credentials.
    ```dotenv
    SPRING_DATASOURCE_URL=jdbc:mysql://phoebe-mysql:3306/phoebe_db?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
    SPRING_DATASOURCE_USERNAME=root
    SPRING_DATASOURCE_PASSWORD=root
    ```

#### Step 3: Run the Project with the Migrated Data

1.  **Start the Docker container** with MySQL:
    ```bash
    docker compose up -d phoebe-mysql
    ```

2.  **Import the new schema**:
    ```bash
    docker exec -i phoebe-mysql mysql -uroot -proot phoebe_db < path/to/your/new/clean_schema.sql
    ```

3.  **Run the application** with the `local` and `mysql` profiles:
    ```bash
    cd backend
    ./gradlew bootRun --args='--spring.profiles.active=local,mysql'
    ```

**Result:** The application will start up using a database populated with your newly migrated data. From here,
you should consider integrating this data into a new, versioned Flyway script.

---

### Scenario D: Backing Up and Restoring Current Work

This process remains manual and is intended for creating full snapshots of your local database.

- All steps and commands are described in detail in the **[Data Backup & Recovery Guide](./DOCKER_DATA_RECOVERY_GUIDE.md)**.

**Important:** Restoring from such a dump can conflict with the state of Flyway migrations, so use this
method with caution, primarily for a full environment transfer.
