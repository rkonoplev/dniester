# Docker Data Backup and Recovery Guide

This document describes the full process of inventorying, backing up (dumping), and restoring
all project databases stored in Docker volumes. This is essential for transferring all work
to another computer or for creating complete backups.

---

## Part 1: Recovering the Original Drupal 6 DB from a "Lost" Volume

This section describes how to create a backup of the very first Drupal 6 database if you suspect it has
been lost, or if the standard migration process creates an empty dump. This can happen if Docker Compose
creates a new, empty volume instead of attaching to an existing one.

### Problem Context

After migration, several Docker volumes may remain on your system. Our task is to find the "golden"
volume containing the original Drupal 6 data and safely extract it.

### Step 1.1: Inventory of All Docker Volumes

First, let's get a list of all volumes to see what we have.

```bash
docker volume ls
```

You will see a list similar to this:
```
DRIVER    VOLUME NAME
local     legacy_mysql_data_drupal6
local     news-platform_mysql_data
local     news-platform_mysql_data_drupal6
local     phoebe_mysql_data
```

- `phoebe_mysql_data`: The main volume for your current project (MySQL 8.0).
- `news-platform_mysql_data_drupal6`: The "golden" volume with the original Drupal 6 data (MySQL 5.7).
- `news-platform_mysql_data`: Likely a volume from the old `news-platform` project.
- `legacy_mysql_data_drupal6`: Most likely a new, empty volume created by a recent run. It can be ignored or deleted.

### Step 1.2: Find the Exact Volume Name

To be 100% sure which volume was attached to your oldest container, run:

```bash
docker inspect news-mysql-drupal6
```

In the output, find the `Mounts` section. The `Name` field will give you the exact volume name you need.

### Step 1.3 (Optional): Investigate the Volume

To ensure the volume actually contains data, you can look inside with a temporary "explorer container".
Replace `<volume_name>` with the name you found in the previous step.

```bash
docker run --rm -it -v <volume_name>:/data ubuntu:latest bash
```

Inside the container, run `cd /data` and `du -sh .`. The size should be substantial (e.g., >200MB).
After checking, exit the container with the `exit` command.

### Step 1.4: Create a Dump from the Correct Volume

Now that we know the exact name of the "golden" volume, we can create a full dump of it. This command
runs a temporary MySQL 5.7 container, mounts your volume to it, and executes `mysqldump`.

Replace `<volume_name>` with yours (e.g., `news-platform_mysql_data_drupal6`).

```bash
docker run --rm -v <volume_name>:/var/lib/mysql -v $(pwd):/backup mysql:5.7 \
sh -c 'mysqld --daemonize && sleep 30 && mysqldump -uroot -proot --all-databases > /backup/drupal6_migration_backup_FULL.sql'
```

- `mysqld --daemonize`: Starts the MySQL server in the background inside the container.
- `sleep 30`: A 30-second pause to give the server time to fully initialize.
- `mysqldump ...`: Creates a dump of all databases and saves it to the `drupal6_migration_backup_FULL.sql`
  file in your current directory on the host machine.

After running this command, you will have a complete and correct backup of your very first database.

---

## Part 2: Creating Dumps for Other Volumes

For a full project transfer, you should also back up the other databases.

### 2.1 Backup of the Current Project DB (MySQL 8.0)

This command creates a dump from your current `phoebe` project volume.

```bash
docker run --rm -v phoebe_mysql_data:/var/lib/mysql -v $(pwd):/backup mysql:8.0 \
sh -c 'mysqld --daemonize && sleep 30 && mysqldump -uroot -proot --all-databases > /backup/phoebe_new_db_backup.sql'
```

- **Result**: A `phoebe_new_db_backup.sql` file with a full backup of the current DB.

### 2.2 Backup of the old `news-platform` DB (Just in Case)

This command creates a dump from the old `news-platform` project volume.

```bash
docker run --rm -v news-platform_mysql_data:/var/lib/mysql -v $(pwd):/backup mysql:8.0 \
sh -c 'mysqld --daemonize && sleep 30 && mysqldump -uroot -proot --all-databases > /backup/news_platform_db_backup.sql'
```

- **Result**: A `news_platform_db_backup.sql` file.

---

## Part 3: Transfer and Restore on Another Computer

After you have copied all `.sql` files to a new computer, you can restore any of the databases.

1. **Create and start the required container**: For example, for the current database:
   ```bash
   docker compose up -d mysql
   ```

2. **Import the dump**:
   ```bash
   docker exec -i <container_name> mysql -uroot -proot < phoebe_new_db_backup.sql
   ```

This procedure ensures a complete and safe transfer of all your work.
