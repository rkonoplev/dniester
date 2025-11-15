> **⚠️ Historical Document**
> 
> This guide describes manual backup and recovery processes that were relevant during the
> early stages of the project. The current project uses **automated Flyway migrations** to create the DB schema, and
> development data is injected through these same migrations.
> 
> **For current Docker data backup and transfer tasks, please refer to:**
> - **[Docker Volume Migration Guide for MySQL Data](./VOLUME_MIGRATION_GUIDE.md)**
> - **[Full Project Transfer Guide](./PROJECT_TRANSFER_GUIDE.md)**
> 
> This document is preserved for historical reference. Following it may lead to conflicts with automatic
> migrations.

# Historical Docker Data Backup and Recovery Guide (Drupal 6 Migration Context)

This document describes the complete process of inventorying, backing up (dumping), and restoring
all project databases stored in Docker volumes, in the context of the Drupal 6 migration.

---

## Part 1: Docker Environment Analysis

During development, several containers and volumes may accumulate in Docker. It is important to understand
which one is responsible for what.

### 1.1 Project Analysis and Purpose

- **Project `phoebe` (main)**
  - **Container**: `phoebe-mysql` (MySQL 8.0)
  - **Volume**: `phoebe_mysql_data`
  - **Purpose**: Your main, current project. It runs the Spring Boot application and uses
    a modern database into which data has been migrated. This is where daily work is conducted.

- **Project `legacy` (migration auxiliary)**
  - **Container**: `phoebe-mysql-drupal6` (MySQL 5.7)
  - **Volume**: `phoebe_mysql_data_drupal6`
  - **Purpose**: This project was created for a single purpose — the migration process. The old Drupal 6 dump
    required an older MySQL version for compatibility. The `legacy` environment is a tool
    for reproducing the migration and is not required for daily work. It should be kept stopped.

### 1.2 Volume Inventory

The `docker volume ls` command will show all data storage. Based on the analysis above, the key ones are:
- `phoebe_mysql_data` (current DB)
- `phoebe_mysql_data_drupal6` (source DB for migration)

---

## Part 2: Recovering the Original Drupal 6 DB from a "Lost" Volume

This section describes how to create a backup of the very first Drupal 6 database if you
suspect it has been lost, or if the standard migration process creates an empty dump.
This can happen if Docker Compose creates a new empty volume instead of connecting an existing one.

### Step 2.1: Find the Exact Volume Name

To be 100% sure which volume was attached to your oldest container, run:

```bash
docker inspect phoebe-mysql-drupal6
```

In the output, find the `Mounts` section. The `Name` field will indicate the exact volume name you need
(e.g., `phoebe_mysql_data_drupal6`).

### Step 2.2 (Optional): Inspecting the Volume

To ensure that the volume actually contains data, you can look inside using a temporary
"explorer container". Replace `<volume_name>` with the name found in the previous step.

```bash
docker run --rm -it -v <volume_name>:/data ubuntu:latest bash
```

Inside the container, run `cd /data` and `du -sh .`. The size should be substantial (e.g., >200MB).
After checking, exit the container with the `exit` command.

### Step 2.3: Create a Dump from the Correct Volume

Now, knowing the exact name of the "golden" volume, we can create its full dump. This command starts
a temporary container with MySQL 5.7, mounts your volume to it, and executes `mysqldump`.

Replace `<volume_name>` with yours (e.g., `phoebe_mysql_data_drupal6`).

```bash
docker run --rm -v <volume_name>:/var/lib/mysql -v $(pwd)/legacy/original_drupal_dump:/backup mysql:5.7 \
sh -c 'mysqld --daemonize && sleep 30 && mysqldump -uroot -proot --all-databases > /backup/drupal6_migration_backup_FULL.sql'
```

- `mysqld --daemonize`: Starts the MySQL server in the background inside the container.
- `sleep 30`: Pause for 30 seconds to give the server time to fully start.
- `mysqldump ...`: Creates a dump of all databases and saves it to the file `drupal6_migration_backup_FULL.sql`
  in your `legacy/original_drupal_dump` directory on the host machine.

After executing this command, you will have a complete and correct backup of your very first database.

---

## Part 3: Creating a Dump of the Current Project DB (MySQL 8.0)

**Important**: Before creating the dump, ensure that the main `phoebe-mysql` container is stopped
to avoid file locking errors (`Unable to lock ./ibdata1`).

1. **Stop the container**:
   ```bash
   docker stop phoebe-mysql
   ```

2. **Create the dump**:
   ```bash
   docker run --rm -v phoebe_mysql_data:/var/lib/mysql -v $(pwd)/legacy/original_drupal_dump:/backup mysql:8.0 \
   sh -c 'mysqld --daemonize && sleep 30 && mysqldump -uroot -proot --all-databases > /backup/phoebe_new_db_backup.sql'
   ```

3. **Restart the container**:
   ```bash
   docker start phoebe-mysql
   ```

- **Result**: The file `phoebe_new_db_backup.sql` with a full backup of the current DB.

---

## Part 4: Transfer and Restore on Another Computer

After you have copied the necessary `.sql` files to a new computer, you can restore any of the databases.

1. **Create and start the required container**: For example, for the current DB:
   ```bash
   docker compose up -d mysql
   ```

2. **Import the dump**:
   ```bash
   docker exec -i <container_name> mysql -uroot -proot < legacy/original_drupal_dump/phoebe_new_db_backup.sql
   ```

This procedure guarantees a complete and safe transfer of all your work.

---

## Part 5: Deep Analysis and Environment Inventory

This section provides a detailed analysis of running containers and volumes so you can
exactly understand what you are working with.

### 5.1 Analysis of Running Projects (`docker ps`)

The `docker ps` output shows two running "projects", each with its own MySQL container:

| Project | Container Name | Image (Version) | Purpose |
| :--- | :--- | :--- | :--- |
| **`legacy`** | `phoebe-mysql-drupal6` | `mysql:5.7` | **Auxiliary**. For migration from old Drupal. |
| **`phoebe`** | `phoebe-mysql` | `mysql:8.0` | **Main**. The current DB for your application. |

For daily work, you **do not need** `legacy` (`phoebe-mysql-drupal6`) to be running. It can be
safely stopped with the command `docker compose -f legacy/docker-compose.drupal.yml down`.

### 5.2 Database Exploration

#### 5.2.1 Exploring the Old DB (`legacy`)

1. **Connect to the container**:
   ```bash
   docker exec -it phoebe-mysql-drupal6 mysql -uroot -proot
   ```
2. **View databases**:
   ```sql
   SHOW DATABASES;
   ```
   You will likely see the `drupal6_legacy` database (old name) and `dniester` (where we imported for cleanup).

3. **View old Drupal tables**:
   ```sql
   USE drupal6_legacy;
   SHOW TABLES;
   ```
   You will see tables such as `node`, `node_revisions`, `term_data`, `users`.

#### 5.2.2 Exploring the New DB (`phoebe`)

1. **Connect to the container**:
   ```bash
   docker exec -it phoebe-mysql mysql -uroot -proot
   ```
2. **View tables**:
   ```sql
   USE phoebe_db;
   SHOW TABLES;
   ```
   You will see tables with new names: `content`, `users`, `roles`, `permissions`, `terms`.

### 5.3 Final Docker Volume Analysis (from Docker Desktop)

| Volume Name | Size | Status | Purpose |
| :--- | :--- | :--- | :--- |
| **`phoebe_mysql_data`** | **201 MB** | **in use** | **Your new, current database (MySQL 8.0).** |
| **`phoebe_mysql_data_drupal6`** | **982.7 MB** | - | **"Golden" archive. The very first, full Drupal 6 dump.** |
| `phoebe_mysql_data` | 628.8 MB | - | Archive of an old project. Less important. |
| `legacy_mysql_data_drupal6` | 210 MB | in use | Intermediate migration database. Can be deleted. |

### 5.4 Summary and Recommendations

- To transfer your work to another computer, you primarily need two files:
  - `phoebe_new_db_backup.sql` (your current work)
  - `drupal6_migration_backup_FULL.sql` (the historical archive)

- The `phoebe_mysql_data` and `legacy_mysql_data_drupal6` volumes can be deleted to free up space,
  as you already have backups of their contents.
  ```bash
  docker volume rm legacy_mysql_data_drupal6
  docker volume rm phoebe_mysql_data
  ```
