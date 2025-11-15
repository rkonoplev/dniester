# Full Project Transfer Guide

This document describes the full and professional process of transferring the entire Phoebe CMS project
to another computer, including the codebase, data, and configuration files.

---

## Transfer Concept

A project transfer consists of two main parts:

1.  **Codebase Transfer**: All files tracked by the Git version control system.
2.  **Data and Configuration Transfer**: Files intentionally excluded from Git via `.gitignore`
    (database dumps, environment variables, logs, IDE files).

A simple `git clone` will only transfer the first part. This guide describes how to transfer both.

---

## Step 1: Preparation for Transfer on the Old Computer

### 1.1 Create Up-to-Date Database Dumps

**Important**: This is the **first and most crucial step**. Before transferring code or creating an archive,
ensure you have fresh backups of all important databases.

Detailed instructions on inventorying and creating dumps for all Docker volumes can be found
in a separate guide:

**[→ Historical Docker Data Backup and Recovery Guide (Drupal 6 Migration Context)](./LEGACY_DOCKER_DATA_RECOVERY_GUIDE_EN.md)**
*   **Note**: For current Docker data backup and transfer tasks, please refer to the **[Docker Volume Migration Guide for MySQL Data](./VOLUME_MIGRATION_GUIDE.md)**.

### 1.2 Synchronize Codebase with GitHub

Ensure all your latest code changes are committed and pushed to GitHub.

```bash
git status
git add .
git commit -m "Prepare for project transfer"
git push
```

### 1.3 Clean Up Temporary Files

Before creating the archive, it is important to delete temporary files and logs to avoid transferring junk.

1.  **Clean Gradle build artifacts**:
    This command will delete the `build/` directory in the backend module.
    ```bash
    ./gradlew clean
    ```

2.  **Clean up logs** (if any):
    ```bash
    # Remove logs if they are in the project root or in backend/logs/
    # Example: rm -rf logs/
    # Example: rm -rf backend/logs/
    ```

3.  **Clean Docker Resources** (optional, if you need to free up space):
    If there are many unused Docker images, containers, or volumes on the old computer, you can perform a full cleanup:
    ```bash
    docker system prune -af --volumes
    ```

### 1.4 Archive Files Not Included in Git

This is the most important step for data transfer. We will create an archive containing all files
ignored by Git.

1.  **Create a list of ignored files**:
    This command finds all files that match the rules in `.gitignore` and writes them to `ignored_files.txt`.
    ```bash
    git ls-files --ignored --exclude-standard > ignored_files.txt
    ```
    **Note**: Ensure your `.gitignore` file is up-to-date and includes all files that *should not* be in Git but *should* be transferred (e.g., `.env` files, DB dumps, IDE configuration files like `.idea/workspace.xml`). Example `.gitignore` content for such files:
    ```
    # Environment variables
    .env
    .env.local

    # Database dumps
    db_dumps/

    # IDE-specific files
    .idea/workspace.xml
    .idea/tasks.xml
    .idea/shelf/
    ```

2.  **Create the archive**:
    This command will create `phoebe_transfer_archive.tar.gz` containing all files from the list.
    ```bash
    tar -czvf phoebe_transfer_archive.tar.gz -T ignored_files.txt
    ```

- **Result**: You will have a single archive `phoebe_transfer_archive.tar.gz` containing all your dumps,
  `.env.dev` files, and other important data.

---

## Step 2: Transfer Data to the New Computer

Transfer the following artifacts to the new computer:

1.  **The URL of your Git repository**.
2.  **The `phoebe_transfer_archive.tar.gz` archive** (e.g., via a USB drive, cloud storage, or `scp`).

---

## Step 3: Deployment on the New Computer

1.  **Clone the repository**:
    ```bash
    git clone <your_repository_URL>
    cd phoebe
    ```

2.  **Check `gradlew` execution permissions**:
    After cloning the repository, the `gradlew` file might lose its execution permissions. Ensure it is executable:
    ```bash
    chmod +x gradlew
    ```

3.  **Extract the archive**:
    Place `phoebe_transfer_archive.tar.gz` in the project root and run the command:
    ```bash
    tar -xzvf phoebe_transfer_archive.tar.gz
    ```
    This command will restore all your ignored files to their original directories (`db_dumps/`, `.env.dev`, etc.).

4.  **Start the Docker containers**:
    ```bash
    docker compose up -d phoebe-mysql
    ```

5.  **Restore the database from the dump**:
    ```bash
    docker exec -i phoebe-mysql mysql -uroot -proot phoebe_db < db_dumps/phoebe_new_db_backup.sql
    ```
    **Note**: Ensure the dump file name (`phoebe_new_db_backup.sql`) matches the file created in Step 1.1.

6.  **Run the application**:
    ```bash
    ./gradlew bootRun --args='--spring.profiles.active=local,mysql'
    ```

Now your project is fully transferred and ready to work on the new computer.

**Note**: The Spring Boot application is typically run directly using `./gradlew bootRun`,
rather than inside a separate Docker container.
