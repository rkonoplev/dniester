# Docker Volume Migration Guide for MySQL Data

This guide describes the process of transferring a Docker Volume containing MySQL data (the `phoebe_db` database) from one working machine to another, as well as steps for data recovery. This is useful when changing computers, recovering from a failure, or synchronizing development environments.

---

## Transfer Concept

Transferring Docker Volume data involves the following stages:

1.  **Exporting Data** from the source machine (creating a database dump).
2.  **Transferring the Dump and Codebase** to the target machine.
3.  **Importing Data** on the target machine.

---

## Part 1: On the Source Machine (Export Data)

These steps are performed on the computer from which you want to transfer the data.

### Step 1: Preparation and Database Dump Creation

Ensure all current changes in the project are committed and pushed to GitHub.

1.  **Stop all Docker Compose services** for the Phoebe project:
    ```bash
    docker compose down
    ```

2.  **Start only the MySQL container** for the Phoebe project:
    ```bash
    docker compose up -d phoebe-mysql
    ```

3.  **Wait for MySQL to be ready**. This may take a few seconds:
    ```bash
    timeout 60s bash -c 'until docker exec phoebe-mysql mysqladmin ping -h localhost --silent; do sleep 2; done'
    ```

4.  **Create a dump of the `phoebe_db` database**. The dump will be saved in the `db_dumps/` folder in the project root. If the folder does not exist, create it:
    ```bash
    mkdir -p db_dumps
    docker exec phoebe-mysql mysqldump -uroot -proot phoebe_db > db_dumps/phoebe_db_backup_$(date +%Y%m%d_%H%M%S).sql
    ```
    *   **Note**: Use `phoebe_db` in the `mysqldump` command to dump only this database. If you want to dump all databases, use `--all-databases` instead of `phoebe_db`.

5.  **Stop the MySQL container**:
    ```bash
    docker compose down
    ```

### Step 2: Prepare Codebase and Dump for Transfer

1.  **Ensure your `.gitignore` file is up-to-date**. It should include `db_dumps/` so that dumps are not committed to Git.
2.  **Copy the created database dump file** (`db_dumps/phoebe_db_backup_*.sql`) to a secure location that will be transferred to the target machine (e.g., external drive, cloud storage, or via `scp`).
3.  **Ensure the entire project codebase is synchronized with the Git repository**.

---

## Part 2: On the Target Machine (Import Data)

These steps are performed on the computer to which you want to transfer the data.

### Step 3: Clone Project and Preparation

1.  **Clone the Phoebe project repository** to the target machine (if not already cloned):
    ```bash
    git clone <your_repository_URL>
    cd phoebe
    ```

2.  **Ensure the `gradlew` script is executable**:
    ```bash
    chmod +x gradlew
    ```

3.  **Create the `db_dumps/` folder** in the project root if it doesn't exist:
    ```bash
    mkdir -p db_dumps
    ```

4.  **Place the database dump file** (`phoebe_db_backup_*.sql`), created in Step 1, into the `db_dumps/` folder on the target machine.

### Step 4: Start MySQL and Import Data

1.  **Start only the MySQL container** for the Phoebe project:
    ```bash
    docker compose up -d phoebe-mysql
    ```

2.  **Wait for MySQL to be ready**:
    ```bash
    timeout 60s bash -c 'until docker exec phoebe-mysql mysqladmin ping -h localhost --silent; do sleep 2; done'
    ```

3.  **Create the `phoebe_db` database** inside the MySQL container if it doesn't already exist:
    ```bash
    docker exec -it phoebe-mysql mysql -uroot -proot -e "CREATE DATABASE IF NOT EXISTS phoebe_db;"
    ```

4.  **Import data from the dump** into the `phoebe_db` database:
    ```bash
    # Replace 'phoebe_db_backup_YYYYMMDD_HHMMSS.sql' with the actual name of your dump file
    docker exec -i phoebe-mysql mysql -uroot -proot phoebe_db < db_dumps/phoebe_db_backup_YYYYMMDD_HHMMSS.sql
    ```

5.  **Stop the MySQL container** (it will be restarted with all services in the next step):
    ```bash
    docker compose down
    ```

### Step 5: Start Project and Verification

1.  **Start all Docker Compose services** for the Phoebe project:
    ```bash
    docker compose up -d
    ```

2.  **Check the status of running containers**:
    ```bash
    docker ps
    ```

3.  **Verify application connection to the database** (e.g., via Health Check):
    ```bash
    curl http://localhost:8080/actuator/health
    ```

4.  **Verify data integrity** in the `phoebe_db` database:
    ```bash
    docker exec phoebe-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM phoebe_db.content;"
    ```

Your Phoebe CMS development environment with MySQL data is now successfully transferred and ready to work on the target machine.

---

## Rollback Plan (if something goes wrong)

If problems occur during the data import process, you can revert to a clean state:

1.  **Stop all Docker Compose services**:
    ```bash
    docker compose down
    ```

2.  **Remove the Docker Volume associated with MySQL** to start fresh. **WARNING: This will delete all data in this Volume!**
    ```bash
    docker volume rm phoebe_mysql_data # Ensure this is the correct Volume name
    ```

3.  **Remove the created `phoebe_db` database** (if it was created but the import failed):
    ```bash
    docker exec -it phoebe-mysql mysql -uroot -proot -e "DROP DATABASE IF EXISTS phoebe_db;"
    ```

After this, you can repeat the data import process from Step 3.

---

## Cleanup (after successful transfer)

After a successful transfer, you can delete the database dump file if it is no longer needed:

```bash
rm db_dumps/phoebe_db_backup_YYYYMMDD_HHMMSS.sql
```
*   **Note**: Replace `phoebe_db_backup_YYYYMMDD_HHMMSS.sql` with the actual name of your dump file.
