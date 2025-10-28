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

**[→ Docker Data Backup and Recovery Guide](./DOCKER_DATA_RECOVERY_GUIDE.md)**

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
    rm -rf logs/
    ```

### 1.4 Archive Files Not Included in Git

This is the most important step for data transfer. We will create an archive containing all files
ignored by Git.

1.  **Create a list of ignored files**:
    This command finds all files that match the rules in `.gitignore` and writes them to `ignored_files.txt`.
    ```bash
    git ls-files --ignored --exclude-standard > ignored_files.txt
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

2.  **Extract the archive**:
    Place `phoebe_transfer_archive.tar.gz` in the project root and run the command:
    ```bash
    tar -xzvf phoebe_transfer_archive.tar.gz
    ```
    This command will restore all your ignored files to their original directories (`db_dumps/`, `.env.dev`, etc.).

3.  **Start the Docker containers**:
    ```bash
    docker compose up -d mysql
    ```

4.  **Restore the database from the dump**:
    ```bash
    docker exec -i news-mysql mysql -uroot -proot < db_dumps/phoebe_new_db_backup.sql
    ```

5.  **Run the application**:
    ```bash
    ./gradlew bootRun --args='--spring.profiles.active=local,mysql'
    ```

Now your project is fully transferred and ready to work on the new computer.
