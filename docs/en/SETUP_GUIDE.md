# Setup Guide

This guide describes how to set up and run the Phoebe CMS project for the first time. There are two main
scenarios: migrating existing data from Drupal 6 or starting with a clean database.

## Initial Project Setup

Before proceeding with any scenario, you need to clone the repository and set up your environment variables.

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/rkonoplev/news-platform.git
    cd news-platform
    ```

2.  **Configure Environment Variables**:
    Copy the example environment file to create your local development environment configuration:
    ```bash
    cp .env.dev.example .env.dev
    ```
    You will then edit `.env.dev` to match your specific database and application settings as described in the scenarios below.

---

## Scenario 1: Migration from Drupal 6 (MySQL Only)

This scenario is for porting an existing Drupal 6 database. The entire migration process is built
around MySQL, so choosing a different database is not possible here.

### Step 1: Perform the Data Migration

Follow the instructions in the [Drupal 6 Migration Guide](MIGRATION_DRUPAL6.md) to obtain the
final `clean_schema.sql` file.

### Step 2: Configure the Environment for MySQL

1.  **Docker Compose**: Ensure the `mysql` service is active in your `docker-compose.yml` file.

2.  **Environment Variables**: Open `.env.dev` (created in the initial setup) and specify your MySQL credentials.
    ```dotenv
    SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/dniester?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
    SPRING_DATASOURCE_USERNAME=root
    SPRING_DATASOURCE_PASSWORD=root
    # ...other variables
    ```

### Step 3: Run the Project

1.  **Start the Docker container** with MySQL:
    ```bash
    docker compose up -d mysql
    ```

2.  **Import the schema**:
    ```bash
    docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
    ```

3.  **Run the application** with the `local` and `mysql` profiles:
    ```bash
    cd backend
    ./gradlew bootRun --args='--spring.profiles.active=local,mysql'
    ```

The application will start up using the existing, migrated database.

---

## Scenario 2: Clean Installation (MySQL or PostgreSQL)

This scenario is for starting from scratch. You can choose any of the supported databases.

### Step 1: Choose Your Database

Decide whether you will use MySQL or PostgreSQL for your project.

### Step 2: Configure Docker Compose

Open your `docker-compose.yml` file and ensure only one database service is active:

- **For MySQL** (keep this block):
  ```yaml
  mysql:
    image: mysql:8.0
    # ... rest of the configuration
  ```

- **For PostgreSQL** (if you added it, keep this block):
  ```yaml
  postgres:
    image: postgres:13
    # ... rest of the configuration
  ```

### Step 3: Configure Environment Variables (`.env.dev`)

Open `.env.dev` (created in the initial setup) and provide the correct datasource URL for your chosen database.

- **For MySQL**:
  ```dotenv
  SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/dniester?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
  SPRING_DATASOURCE_USERNAME=root
  SPRING_DATASOURCE_PASSWORD=root
  ```

- **For PostgreSQL**:
  ```dotenv
  SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/dniester
  SPRING_DATASOURCE_USERNAME=user
  SPRING_DATASOURCE_PASSWORD=password
  ```

### Step 4: Run the Application with the Correct Profile

1.  **Start the Docker container** with your chosen database:
    ```bash
    # For MySQL
    docker compose up -d mysql

    # For PostgreSQL
    docker compose up -d postgres
    ```

2.  **Run the application**, specifying the corresponding database profile:
    ```bash
    cd backend

    # For MySQL
    ./gradlew bootRun --args='--spring.profiles.active=local,mysql'

    # For PostgreSQL
    ./gradlew bootRun --args='--spring.profiles.active=local,postgresql'
    ```

On the first run, Flyway will automatically create the entire table structure in your database.
