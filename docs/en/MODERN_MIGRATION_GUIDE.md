# Modern Guide to Migrating Data from Drupal 6

> **🎯 Goal:** To quickly set up the project from scratch, using data from the original Drupal 6 dump,
> with a modern architecture based on **Flyway** automated migrations.

---

## Concept

Unlike the old, fully manual process, this method relies on automation. Data from the old Drupal 6 dump
is no longer imported directly into the database. Instead, it has been converted into a special Flyway
migration script (`V3__insert_sample_data.sql`), which is part of the project's source code.

This ensures that any developer starting the project from scratch will get an identical database already
populated with historical data, without any manual SQL dump manipulation.

---

## Zero-to-Hero Deployment Process

For a new developer who needs to get the project running locally with a full database, the process is
now extremely simple.

### Step 1: Prepare the Environment

1.  **Clone the repository:**
    ```bash
    git clone <your_repository_URL>
    cd phoebe
    ```

2.  **Ensure Docker is running.**

### Step 2: Launch the Project

Execute a single command that will start both the database and the application:

```bash
docker compose up --build
```

### What Happens Automatically?

1.  **Build and Run Containers:** `docker-compose` will build the image for your Spring Boot application and start
    two containers: `news-app` (the application) and `news-mysql` (the MySQL 8.0 database).

2.  **Create an Empty Database:** On its first run, the `news-mysql` container will create an empty database
    named `dniester`.

3.  **Automated Flyway Migrations:**
    - Your Spring Boot application (`news-app`) will start and connect to the empty database.
    - **Flyway**, which is integrated into the application, will detect that the database is empty.
    - Flyway will sequentially execute all SQL scripts from the `backend/src/main/resources/db/migration/common` folder:
      - `V1__initial_schema.sql`: Will create the entire table structure (`users`, `content`, etc.).
      - `V3__insert_sample_data.sql`: Will **populate these tables with data**, including users, roles, and content
        that was converted from the original Drupal 6 dump.
      - Subsequent scripts (`V4`, `V5`, etc.) will apply all further schema changes.

### Step 3: Verify the Result

After all containers have started successfully, your database will be fully ready and populated with data.

You can verify this by connecting to the database:

```bash
# Connect to the database container
docker exec -it news-mysql mysql -uroot -proot

# Inside MySQL, run the commands
USE dniester;
SELECT COUNT(*) FROM content; -- Should show ~12186 records
SELECT COUNT(*) FROM users;   -- Should show ~400+ users
```

## Conclusion

This automated approach eliminates the need for manual SQL dump imports, ensures database consistency across
all developers, and is standard practice in modern projects.

The old migration guide (`MIGRATION_DRUPAL6.md`) is preserved in the archive to provide insight into how
the data from `drupal6-working.sql` was once converted into a Flyway migration script.
