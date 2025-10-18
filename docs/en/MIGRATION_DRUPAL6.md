# Migration Guide: Drupal 6 → Phoebe CMS

This detailed step-by-step guide describes the content migration process from a legacy Drupal 6
system to the modern headless Phoebe CMS architecture using Docker and MySQL.

## Table of Contents
- [Overall Process Flow](#overall-process-flow)
- [Quick Version (TL;DR)](#quick-version-tldr)
- [Complete Migration Guide](#complete-migration-guide)
- [Description of Data Structure After Migration](#description-of-data-structure-after-migration)
- [Troubleshooting](#troubleshooting)

---

## Overall Process Flow

The migration process is built on the Extract, Transform, Load (ETL) principle and proceeds as follows:

1.  **Launch Temporary Environment**: We start a MySQL 5.7 container, compatible with the Drupal 6 dump.
2.  **Import and Normalize**: The source dump is loaded, and SQL scripts are applied to clean and
    transform the data into the new, modern schema.
3.  **Export Clean Dump**: A final SQL file (`clean_schema.sql`) is created, containing the ready
    structure and data.
4.  **Deploy Target Environment**: The main MySQL 8.0 container is launched.
5.  **Final Import**: `clean_schema.sql` is loaded into the new database.

---

## Quick Version (TL;DR)

This section is for those who already have a ready `clean_schema.sql` file.

```bash
# 1. Start the target MySQL 8.0 database container
docker compose -f docker-compose.yml up -d mysql

# 2. Import the cleaned schema and data into the 'dniester' database
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql

# 3. Verify that the data is in place (e.g., check the number of articles)
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```

---

## Complete Migration Guide

### Step 1: Launch Temporary MySQL 5.7 for Drupal 6 Dump

Old Drupal version dumps may be incompatible with the latest MySQL versions due to syntax differences.
Therefore, we use a temporary MySQL 5.7 container to ensure compatibility.

```bash
# Launch the container in the background. The docker-compose.drupal.yml file is specifically
# configured for this task.
docker compose -f docker-compose.drupal.yml up -d

# Monitor logs to ensure the server has started successfully
docker logs -f news-mysql-drupal6
```

### Step 2: Import and Normalize Data

Now we load the original Drupal dump into our temporary database and apply SQL scripts to
transform it.

```bash
# Import the original dump (e.g., drupal6_fixed.sql) into the 'dniester' database
# inside our temporary container.
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/drupal6_fixed.sql

# Apply the main normalization script. It creates new tables and transfers cleaned
# data from old Drupal tables.
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/migrate_from_drupal6_universal.sql

# If your Drupal site used the CCK module for custom fields,
# apply this additional script for their migration.
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/migrate_cck_fields.sql
```

### Step 3: Export Cleaned Schema (`clean_schema.sql`)

After all data has been transformed, we create the final, "clean" dump. This file is the main
artifact of the entire migration process.

```bash
# Create a dump of the 'dniester' database from the temporary container and save it to a file.
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot dniester > db_data/clean_schema.sql
```

### Step 4: Prepare and Launch Target Environment (MySQL 8.0)

The temporary environment is no longer needed. We stop it and launch the project's main database.

```bash
# Stop and completely remove the temporary container and its volume.
docker compose -f docker-compose.drupal.yml down -v

# Launch the main MySQL 8.0 container, which will be used by the application.
docker compose -f docker-compose.yml up -d mysql
```

### Step 5: Import Final Dump into MySQL 8.0

Load our `clean_schema.sql` into the new, main database.

```bash
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
```

### Step 6: Verify the Result

The final step is to ensure that all data has been successfully transferred to the new database.

```bash
# Check the number of articles in the content table
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester

# Check the number of users
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM users;" dniester
```

---

## Description of Data Structure After Migration

After executing the normalization scripts, data from Drupal 6 will be transformed into a new,
more structured schema.

- **Users (`users`)**: Basic user information (login, email, status).
- **Roles (`roles`)**: Roles such as ADMIN, EDITOR.
- **Content (`content`)**: All content types (articles, pages, books) are unified into one table.
- **Taxonomy (`terms`)**: Terms (categories, tags) with preserved original vocabularies (`vocabulary`).
- **Join Tables**: `user_roles`, `content_terms`, `role_permissions` for managing relationships.

> For a complete description of the final schema, refer to the [Database Guide](./DATABASE_GUIDE.md).

---

## Troubleshooting

### UTF-8 Encoding Issues
If import errors like `Incorrect string value` occur, it means your database uses an incorrect
default encoding (e.g., `latin1`). Ensure all tables are created with `utf8mb4` encoding.

### MySQL 8.0 Root Password Issues
Sometimes, after container initialization, authentication issues with the `root` user may arise.
In this case, a manual password reset using the `--skip-grant-tables` flag might be necessary.
Detailed instructions can be found in the [Setup Guide](./SETUP_GUIDE.md).
