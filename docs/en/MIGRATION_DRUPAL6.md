# Migration Guide: Drupal 6 → Phoebe CMS

This detailed step-by-step guide describes the content migration process from a legacy Drupal 6
system to the modern headless Phoebe CMS architecture using Docker and MySQL.

## Table of Contents
- [Overall Process Flow](#overall-process-flow)
- [Quick Version (TL;DR)](#quick-version-tldr)
- [Complete Migration Guide](#complete-migration-guide)
- [Complete Migration Pipeline](#complete-migration-pipeline)
- [Description of Data Structure After Migration](#description-of-data-structure-after-migration)
- [Troubleshooting](#troubleshooting)
- [Reference](#reference)

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

## Complete Migration Pipeline

For a full end-to-end migration from a Drupal 6 dump to a clean MySQL 8.0 Phoebe CMS database, you can execute the following sequence of commands:

```bash
# 1. Start Drupal 6 migration environment (MySQL 5.7 container)
docker compose -f docker-compose.drupal.yml up -d

# 2. Export original Drupal 6 data, import into a clean database, and run normalization scripts
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot a264971_dniester > db_data/drupal6_fixed.sql
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/drupal6_fixed.sql
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/migrate_from_drupal6_universal.sql
# Optional: If you had CCK fields in Drupal 6, uncomment and run the following line:
# docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/migrate_cck_fields.sql

# 3. Export the cleaned and normalized schema to clean_schema.sql
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot dniester > db_data/clean_schema.sql

# 4. Stop and remove the temporary Drupal 6 migration environment
docker compose -f docker-compose.drupal.yml down -v

# 5. Start the target MySQL 8.0 container for Phoebe CMS
docker compose -f docker-compose.yml up -d mysql

# 6. Import the final cleaned schema into MySQL 8.0
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql

# 7. Verify the migration (e.g., count content rows)
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
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

### UTF-8 / Cyrillic Encoding Issues

If you encounter errors like `ERROR 1366 (HY000): Incorrect string value: '\xD0\x98\xD0\xBD...' for column 'title'`, it means that MySQL created your target table with an incorrect default collation (e.g., `latin1`). By default, MySQL 5.7 (and sometimes 8.0 depending on configuration) might use `latin1` unless explicitly specified.

#### Step 1: Check Table Encoding
Inside your MySQL container (e.g., `news-mysql-drupal6` or `news-mysql`):
```sql
SHOW CREATE TABLE a264971_dniester.node \G
```
Usually, Drupal 6 tables are `DEFAULT CHARSET=utf8`. Now, check your new `content` table (it likely has `DEFAULT CHARSET=latin1`).

#### Step 2: Recreate the Target Table with UTF-8
If you find encoding issues, you might need to drop and re-create the affected table with the correct character set and collation. For example, for the `content` table:

```sql
DROP TABLE IF EXISTS content;

CREATE TABLE content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body TEXT,
    teaser TEXT,
    publication_date DATETIME NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME,
    updated_at DATETIME,
    version BIGINT,
    author_id BIGINT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### Step 3: Retry Data Insertion
After recreating the table with the correct encoding, retry the data insertion step from the migration guide. With `utf8mb4_unicode_ci`, Cyrillic and other multi-byte characters should be inserted correctly.

### MySQL 8.0 Root Password Issues

Sometimes, after container initialization, authentication issues with the `root` user may arise (e.g., if MySQL 8.0 creates the root user with an empty password or socket authentication by default). In this case, a manual password reset using the `--skip-grant-tables` flag might be necessary.

#### Step 1: Stop the MySQL Container
```bash
docker stop news-mysql
```

#### Step 2: Start a Temporary Container with `skip-grant-tables`
This allows you to connect to MySQL without password authentication.
```bash
docker run -it --rm \
--name mysql-fix \
-v news-platform_mysql_data:/var/lib/mysql \
mysql:8.0 \
--skip-grant-tables --skip-networking
```

#### Step 3: Connect to the Temporary MySQL Instance
Open a **new terminal window** and connect to the temporary container:
```bash
docker exec -it mysql-fix mysql
```

#### Step 4: Reset the Root Password
Inside the MySQL prompt, execute the following commands to flush privileges and set a new password for the `root` user (e.g., `root`):
```sql
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
```

#### Step 5: Stop the Temporary Container and Restart Main MySQL
Exit the MySQL prompt, then stop the `mysql-fix` container (Ctrl+C in the first terminal or `docker stop mysql-fix`). Finally, restart your main `news-mysql` container:
```bash
docker compose -f docker-compose.yml up -d mysql
```

---

## Reference

### Legacy Files Relocation

After successful migration completion, several files that were used during the migration process
have been moved to the `legacy/` folder for historical reference:

- **`DatabaseProperties.java`**: Custom database configuration class used during migration.
- **`Makefile`**: Legacy API testing utility with outdated endpoints.
- **`docker-compose.drupal.yml`**: Temporary Docker setup for MySQL 5.7 compatibility.
- **`docker-compose.override.yml`**: Production Docker configuration with security enhancements.
- **`ExampleTest.java`**: Initial placeholder test file from early development.

These files are preserved for reference and understanding the migration evolution.
See `legacy/README.md` for detailed descriptions.

### Migration SQL Files

During the migration process, several SQL scripts have been created and used. Each has a specific purpose:

For detailed information about all migration scripts and files, see [Database Migration Scripts](../db_data/README.md).

- **`drupal6_fixed.sql`**:
  Cleaned snapshot of the original Drupal 6 database (`a264971_dniester`) imported into the
  temporary MySQL 5.7 instance. Purpose: normalize database name and ensure compatibility.

- **`migrate_from_drupal6_universal.sql`**:
  Main migration script. It creates a new clean schema (`users`, `roles`, `user_roles`, `content`,
  `terms`, `content_terms`) and moves normalized data from the old Drupal tables. Core of the migration.

- **`detect_custom_fields.sql`**:
  Optional helper script. Runs queries against `information_schema` to detect if there are any
  `content_type_*` tables with additional CCK fields. Note: If this script selects rows, it means
  you had custom fields. If the result set is empty, you had **no CCK custom fields** in your Drupal 6 dump.

- **`migrate_cck_fields.sql`**:
  Optional script. Only needed if CCK fields exist. It copies fields from `content_type_*` tables
  into the generic `custom_fields` table (key → value model). If you had no CCK, this script can be ignored.

- **`clean_schema.sql`**:
  Final export after applying `migrate_from_drupal6_universal.sql` (and optionally `migrate_cck_fields.sql`).
  This is the schema and data used in MySQL 8.0 by Phoebe CMS.

### Verifying Custom Fields

It's crucial to verify whether custom fields (CCK) from your Drupal 6 installation were present and correctly migrated.

#### 1. Verifying in MySQL 5.7 (Drupal Container)
If your temporary Drupal 6 container is still running, you can check for `content_type_*` tables:
```sql
SHOW TABLES LIKE 'content_type%';
```
- If you see rows like `content_type_article`, `content_type_news`, etc., it indicates you had custom CCK fields.
- If nothing is returned, you did not use Drupal CCK, and the `custom_fields` table will remain empty and can be ignored.

#### 2. Checking Custom Fields in New Database (MySQL 8.0)
If the original Drupal 6 container has already been stopped or deleted, you cannot inspect `content_type_*` tables directly. However, you can still verify if any CCK fields were migrated by checking the `custom_fields` table in your new `dniester` schema:

```sql
SELECT COUNT(*) FROM custom_fields;
```
- If the result is `0`, you had no custom CCK fields, or their migration was skipped.
- If the result is greater than `0`, you had custom fields in Drupal 6, and they were migrated into `custom_fields` as key→value records.

### Post-Migration Cleanup

After completing the migration and successfully importing `clean_schema.sql` into MySQL 8.0, it's good practice to clean up the temporary Drupal 6 environment to free up resources.

#### Docker Volumes

- **`news-platform_mysql_data`**: **Keep this volume**. It is used by your main MySQL 8.0 container (`news-mysql`) for Phoebe CMS data.
- **`news-platform_mysql_data_drupal6`**: **This can be safely removed**. It's a leftover from the Drupal 6 migration process and is no longer needed.

#### Cleanup Options

**Option A — Just stop the Drupal 6 container (keep volume just in case):**
This is a softer approach, keeping the volume in case you need to re-examine it later.
```bash
docker stop news-mysql-drupal6
```

**Option B — Completely remove the Drupal 6 container and its volume (recommended after successful export):**
This option frees up disk space and is recommended once you are confident the migration is complete and successful.
```bash
docker compose -f docker-compose.drupal.yml down -v
```
After cleanup, only MySQL 8.0 (`news-mysql`) and its persistent volume (`news-platform_mysql_data`) should remain for further work with Phoebe CMS.
