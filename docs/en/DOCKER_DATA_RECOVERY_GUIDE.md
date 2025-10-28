# Docker Data Backup and Recovery Guide

This document describes the full process of inventorying, backing up (dumping), and restoring
all project databases stored in Docker volumes. This is the **first step** in the process of a full
project transfer to another computer.

> **Next Step**: After creating data backups, refer to the
> **[Full Project Transfer Guide](./PROJECT_TRANSFER_GUIDE.md)**
> for instructions on transferring the codebase and deploying on a new machine.

---

## Part 1: Analysis of the Docker Environment

During development, Docker can accumulate several containers and volumes. It is important to understand
which one is which.

### 1.1 Analysis of Projects and Their Purpose

- **`phoebe` Project (Main)**
  - **Containers**: `phoebe-app-1`, `news-mysql` (MySQL 8.0)
  - **Volume**: `phoebe_mysql_data`
  - **Purpose**: Your main, current project. It runs the Spring Boot application and uses the modern
    database into which the data was migrated. This is the one used for daily work.

- **`legacy` Project (Migration Helper)**
  - **Container**: `news-mysql-drupal6` (MySQL 5.7)
  - **Volume**: `news-platform_mysql_data_drupal6`
  - **Purpose**: This project was created for one purpose only: the migration process. The old Drupal 6
    dump required an old version of MySQL for compatibility. The `legacy` environment is a tool
    for reproducing the migration and is not required for daily work. It should be kept stopped.

### 1.2 Volume Inventory

The `docker volume ls` command will show all data stores. Based on the analysis above, the key volumes are:
- `phoebe_mysql_data` (current DB)
- `news-platform_mysql_data_drupal6` (original DB for migration)

---

## Part 2: Recovering the Original Drupal 6 DB from a "Lost" Volume

This section describes how to create a backup of the very first Drupal 6 database if you suspect it has
been lost, or if the standard migration process creates an empty dump. This can happen if Docker Compose
creates a new, empty volume instead of attaching to an existing one.

### Step 2.1: Find the Exact Volume Name

To be 100% sure which volume was attached to your oldest container, run:

```bash
docker inspect news-mysql-drupal6
```

In the output, find the `Mounts` section. The `Name` field will give you the exact volume name you need
(e.g., `news-platform_mysql_data_drupal6`).

### Step 2.2 (Optional): Investigate the Volume

To ensure the volume actually contains data, you can look inside with a temporary "explorer container".
Replace `<volume_name>` with the name you found in the previous step.

```bash
docker run --rm -it -v <volume_name>:/data ubuntu:latest bash
```

Inside the container, run `cd /data` and `du -sh .`. The size should be substantial (e.g., >200MB).
After checking, exit the container with the `exit` command.

### Step 2.3: Create a Dump from the Correct Volume

Now that we know the exact name of the "golden" volume, we can create a full dump of it. This command
runs a temporary MySQL 5.7 container, mounts your volume to it, and executes `mysqldump`.

Replace `<volume_name>` with yours (e.g., `news-platform_mysql_data_drupal6`).

```bash
docker run --rm -v <volume_name>:/var/lib/mysql -v $(pwd)/db_dumps:/backup mysql:5.7 \
sh -c 'mysqld --daemonize && sleep 30 && mysqldump -uroot -proot --all-databases > /backup/drupal6_migration_backup_FULL.sql'
```

- `mysqld --daemonize`: Starts the MySQL server in the background inside the container.
- `sleep 30`: A 30-second pause to give the server time to fully initialize.
- `mysqldump ...`: Creates a dump of all databases and saves it to the `drupal6_migration_backup_FULL.sql`
  file in your `db_dumps` directory on the host machine.

After running this command, you will have a complete and correct backup of your very first database.

---

## Part 3: Creating a Dump of the Current Project DB (MySQL 8.0)

**Important**: Before creating a dump, ensure that the main container `news-mysql` is stopped
to avoid file lock errors (`Unable to lock ./ibdata1`).

1. **Stop the container**:
   ```bash
   docker stop news-mysql
   ```

2. **Create the dump**:
   ```bash
   docker run --rm -v phoebe_mysql_data:/var/lib/mysql -v $(pwd)/db_dumps:/backup mysql:8.0 \
   sh -c 'mysqld --daemonize && sleep 30 && mysqldump -uroot -proot --all-databases > /backup/phoebe_new_db_backup.sql'
   ```

3. **Restart the container**:
   ```bash
   docker start news-mysql
   ```

- **Result**: A `phoebe_new_db_backup.sql` file with a full backup of the current DB.

---

## Part 4: Transfer and Restore on Another Computer

After you have copied the required `.sql` files to a new computer, you can restore any of the databases.

1. **Create and start the required container**: For example, for the current database:
   ```bash
   docker compose up -d mysql
   ```

2. **Import the dump**:
   ```bash
   docker exec -i <container_name> mysql -uroot -proot < phoebe_new_db_backup.sql
   ```

This procedure ensures a complete and safe transfer of all your work.

---

## Part 5: Deep Dive and Environment Inventory

This section provides a detailed analysis of the running containers and volumes so you can
understand exactly what you are working with.

### 5.1 Analysis of Running Projects (`docker ps`)

The output of `docker ps` shows two running "projects", each with its own MySQL container:

| Project | Container Name | Image (Version) | Purpose |
| :--- | :--- | :--- | :--- |
| **`legacy`** | `news-mysql-drupal6` | `mysql:5.7` | **Helper**. For migrating from old Drupal. |
| **`phoebe`** | `news-mysql` | `mysql:8.0` | **Main**. The current database for your application. |

For daily work, you do **not** need the `legacy` (`news-mysql-drupal6`) project to be running. You can
safely stop it with `docker compose -f legacy/docker-compose.drupal.yml down`.

### 5.2 Database Exploration

#### 5.2.1 Exploring the Old DB (`legacy`)

1. **Connect to the container**:
   ```bash
   docker exec -it news-mysql-drupal6 mysql -uroot -proot
   ```
2. **Show databases**:
   ```sql
   SHOW DATABASES;
   ```
   You will likely see the `a264971_dniester` (old name) and `dniester` (where it was imported for cleaning) databases.

3. **Show old Drupal tables**:
   ```sql
   USE a264971_dniester;
   SHOW TABLES;
   ```
   You will see tables like `node`, `node_revisions`, `term_data`, `users`.

#### 5.2.2 Exploring the New DB (`phoebe`)

1. **Connect to the container**:
   ```bash
   docker exec -it news-mysql mysql -uroot -proot
   ```
2. **Show tables**:
   ```sql
   USE dniester;
   SHOW TABLES;
   ```
   You will see tables with new names: `content`, `users`, `roles`, `permissions`, `terms`.

### 5.3 Final Docker Volume Analysis (from Docker Desktop)

| Volume Name | Size | Status | Purpose |
| :--- | :--- | :--- | :--- |
| **`phoebe_mysql_data`** | **201 MB** | **in use** | **Your new, current database (MySQL 8.0).** |
| **`news-platform_mysql_data_drupal6`** | **982.7 MB** | - | **"Golden" archive. The very first, full Drupal 6 dump.** |
| `news-platform_mysql_data` | 628.8 MB | - | Archive of an old project. Less important. |
| `legacy_mysql_data_drupal6` | 210 MB | in use | Intermediate migration database. Can be deleted. |

### 5.4 Summary and Recommendations

- To transfer your work to another computer, you primarily need two files:
  - `phoebe_new_db_backup.sql` (your current work)
  - `drupal6_migration_backup_FULL.sql` (the historical archive)

- The `news-platform_mysql_data` and `legacy_mysql_data_drupal6` volumes can be deleted to free up space,
  as you already have backups of their contents.
  ```bash
  docker volume rm legacy_mysql_data_drupal6
  docker volume rm news-platform_mysql_data
  ```
