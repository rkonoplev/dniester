# Appendix: Recovering the Original Drupal 6 DB from a Docker Volume

This document describes how to create a backup of the very first Drupal 6 database if you suspect it has
been lost, or if the standard migration process creates an empty dump. This can happen if Docker Compose
creates a new, empty volume instead of attaching to an existing one.

### Problem Context

After migration, several Docker volumes may remain on your system. Our task is to find the "golden"
volume containing the original Drupal 6 data and safely extract it.

### Step 1: Inventory of All Docker Volumes

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

- `phoebe_mysql_data`: The volume for your current project (MySQL 8.0).
- `legacy_mysql_data_drupal6`: Most likely a new, empty volume created by a recent run.
- `news-platform_mysql_data_drupal6`: **The most likely candidate** for the "golden" volume with the source data.

### Step 2: Find the Exact Volume Name

To be 100% sure which volume was attached to your oldest container, run:

```bash
docker inspect news-mysql-drupal6
```

In the output, find the `Mounts` section. The `Name` field will give you the exact volume name you need.

### Step 3 (Optional): Investigate the Volume

To ensure the volume actually contains data, you can look inside with a temporary "explorer container".
Replace `<volume_name>` with the name you found in the previous step.

```bash
docker run --rm -it -v <volume_name>:/data ubuntu:latest bash
```

Inside the container, run `cd /data` and `du -sh .`. The size should be substantial (e.g., >200MB).
After checking, exit the container with the `exit` command.

### Step 4: Create a Dump from the Correct Volume

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
