# Development Guide with Local Database (without Docker)

This guide describes how to set up and conduct development for the Phoebe CMS project if you prefer to use locally installed MySQL or PostgreSQL databases instead of Docker containers.

---

## Table of Contents
- [Prerequisites](#prerequisites)
- [Step 1: Clone the Project](#step-1-clone-the-project)
- [Step 2: Configure Local Database](#step-2-configure-local-database)
- [Step 3: Configure Spring Boot Project](#step-3-configure-spring-boot-project)
- [Step 4: Run the Application](#step-4-run-the-application)
- [Workflow](#workflow)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

Before you begin, ensure the following are installed on your computer:

1.  **Java Development Kit (JDK) 21** or higher.
2.  **Gradle 8.x** (usually provided with the project via `gradlew`, but ensure it's functional).
3.  **Locally installed database**:
    *   **MySQL 8.0** or higher, OR
    *   **PostgreSQL 12** or higher.
4.  **Database management tool** (recommended):
    *   For MySQL: **phpMyAdmin**, **MySQL Workbench**, or similar.
    *   For PostgreSQL: **pgAdmin**, or similar.
5.  **Git**.

---

## Step 1: Clone the Project

If you haven't already, clone the Phoebe project repository:

```bash
git clone <your_repository_URL>
cd phoebe
```

Ensure the `gradlew` script is executable:
```bash
chmod +x gradlew
```

---

## Step 2: Configure Local Database

You will need to create a database and a user for the Phoebe project.

### For MySQL

1.  **Connect to your local MySQL server** (e.g., via MySQL Workbench, phpMyAdmin, or the command line `mysql -u root -p`).
2.  **Create the `phoebe_db` database**:
    ```sql
    CREATE DATABASE IF NOT EXISTS phoebe_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```
3.  **Create a user `phoebe_user` and grant privileges** (replace `your_password` with a strong password):
    ```sql
    CREATE USER 'phoebe_user'@'localhost' IDENTIFIED BY 'your_password';
    GRANT ALL PRIVILEGES ON phoebe_db.* TO 'phoebe_user'@'localhost';
    FLUSH PRIVILEGES;
    ```
    *   **Note**: If you are not connecting from `localhost`, replace `localhost` with the appropriate IP address or `%`.

### For PostgreSQL

1.  **Connect to your local PostgreSQL server** (e.g., via pgAdmin or the command line `psql -U postgres`).
2.  **Create the `phoebe_db` database**:
    ```sql
    CREATE DATABASE phoebe_db;
    ```
3.  **Create a user `phoebe_user` and grant privileges** (replace `your_password` with a strong password):
    ```sql
    CREATE USER phoebe_user WITH PASSWORD 'your_password';
    GRANT ALL PRIVILEGES ON DATABASE phoebe_db TO phoebe_user;
    ```

---

## Step 3: Configure Spring Boot Project

You will need to create or modify a configuration file for Spring Boot to connect to your local database.

1.  **Create a file named `application-local-db.yml`** in the `backend/src/main/resources/` directory.
2.  **Add the following configuration** depending on your database:

### For MySQL (`application-local-db.yml`)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/phoebe_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: phoebe_user
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate # Or update for automatic schema updates (use with caution)
  flyway:
    enabled: true
    locations: classpath:db/migration/common,classpath:db/migration/mysql
```
*   **Important**: Replace `your_password` with the password you set for `phoebe_user`.

### For PostgreSQL (`application-local-db.yml`)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/phoebe_db
    username: phoebe_user
    password: your_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate # Or update for automatic schema updates (use with caution)
  flyway:
    enabled: true
    locations: classpath:db/migration/common,classpath:db/migration/postgresql
```
*   **Important**: Replace `your_password` with the password you set for `phoebe_user`.
*   **Note**: If your PostgreSQL is running on a different port, change `5432` accordingly.

---

## Step 4: Run the Application

You are now ready to run the Spring Boot application using your new `local-db` profile.

1.  **Navigate to the `backend` directory**:
    ```bash
    cd backend
    ```

2.  **Run the application**:
    ```bash
    ./gradlew bootRun --args='--spring.profiles.active=local-db'
    ```
    *   On the first run, Flyway will automatically apply all necessary migrations to your database, creating the schema and populating initial data.

3.  **Check application status**:
    Open `http://localhost:8080/actuator/health` in your browser or use `curl`:
    ```bash
    curl http://localhost:8080/actuator/health
    ```
    You should see a status of `UP`.

---

## Workflow

When developing with a local database, your workflow will look like this:

1.  **Start your local MySQL/PostgreSQL server**.
2.  **Run the Spring Boot application** with the `local-db` profile:
    ```bash
    cd backend
    ./gradlew bootRun --args='--spring.profiles.active=local-db'
    ```
3.  **Write code and test**. The application will interact directly with your local database.
4.  **For unit tests**:
    ```bash
    cd backend
    ./gradlew test
    ```
    *   Unit tests do not require a running database.
5.  **For integration tests**:
    *   Integration tests are configured by default to use Testcontainers. If you want them to use your local database, you would need to modify their configuration (which is not recommended, as Testcontainers provide an isolated and reproducible environment).
    *   If you still wish to run integration tests without Testcontainers, you would need to create a separate profile for tests that points to your local database and ensure your local database is clean before each test run.

---

## Troubleshooting

*   **"Access denied for user 'phoebe_user'@'localhost'"**: Ensure the username and password in `application-local-db.yml` match those you set in the database. Verify that the user has access from `localhost` (or the appropriate IP).
*   **"Unknown database 'phoebe_db'"**: Ensure you have created the `phoebe_db` database on your local server.
*   **"Connection refused"**: Ensure your local MySQL/PostgreSQL server is running and accessible on the specified port (`3306` for MySQL, `5432` for PostgreSQL). Check firewall settings.
*   **"Flyway migration failed"**: This can happen if the database schema does not match what Flyway expects. Ensure the `phoebe_db` database is empty before the first Flyway run, or that you are not trying to apply migrations to an already modified schema. In case of problems, you can try to clean the Flyway schema:
    ```bash
    cd backend
    ./gradlew flywayClean --args='--spring.profiles.active=local-db'
    ```
    **WARNING**: `flywayClean` will delete all tables from the database specified in the `local-db` profile! Use with caution.

---

This guide should help you set up and conduct development for the Phoebe CMS project using a locally installed database.
