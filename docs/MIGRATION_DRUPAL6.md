## 🚀 Migration Guide: Drupal 6 → News Platform (Spring Boot + MySQL 8)
### 1. Lift the temporary MySQL 5.7 for Drupal 6 dump
   ```bash
   docker compose -f docker-compose.drupal.yml up -d
   docker logs -f news-mysql-drupal6
   ```
   Login:

```bash
docker exec -it news-mysql-drupal6 mysql -u root -p
# password: root
```
Check source DB:

```sql
SHOW DATABASES;
USE a264971_dniester;
SHOW TABLES;
```
### 2. Export original data and import into clean schema
   ```bash
# Export from old schema
docker exec -i news-mysql-drupal6 mysqldump -u root -proot a264971_dniester > db_data/drupal6_fixed.sql

# Import into dniester
docker exec -i news-mysql-drupal6 mysql -u root -proot dniester < db_data/drupal6_fixed.sql
```
Check:

```bash
docker exec -it news-mysql-drupal6 mysql -uroot -proot -e "USE dniester; SHOW TABLES;"
```
At this point you can run your migration SQL scripts to normalize DB.
Run migration scripts:

```sql
source db_data/migrate_from_drupal6_universal.sql;
source db_data/migrate_cck_fields.sql; -- optional
```

---

### 📦 What We Imported into the New Schema

After normalizing the dump, the following entities and tables exist in the clean `dniester` schema. These form the core database for the News Platform.

#### 1. Users
**Source:** Drupal `users`  
**Target:** `users`  
**Fields imported:**
- `uid` → `id` (PK)
- `name` → `username`
- `mail` → `email`
- `status` → `status` (tinyint: 1=active, 0=blocked)

#### 2. Roles & User_Roles
**Source:** Drupal `role`, `users_roles`  
**Target:**
- `roles` (id, name)
- `user_roles` (user_id, role_id)

Many‑to‑many mapping between users and roles.

#### 3. Content
**Source:** `node`, `node_revisions`  
**Target:** `content` (unified table, no “type” field anymore)  
**Fields imported:**
- `nid` → `id`
- `title` → `title`
- `body` → `body`
- `teaser` → `teaser`
- `created` (UNIX ts) → `publication_date` (DATETIME)
- `uid` → `author_id` (FK → users.id)

> Note: Drupal `story` / `page` / `book` types were merged → all stored in one `content` table.

#### 4. Terms & Content_Terms
**Source:** `term_data`, `vocabulary`, `term_node`  
**Target:**
- `terms` (id, name, vocabulary)
- `content_terms` (content_id, term_id)

Taxonomy terms and mapping table for content↔terms.

#### 5. Custom Fields (CCK)
**Source:** Drupal `content_type_*` tables (if present).  
**Target:** `custom_fields` (generic key→value model).  
**Fields imported:**
- `nid` → `content_id`
- Column name → `field_name`
- Column value → `field_value`

---

### ✅ Summary of Final Tables
- `users`
- `roles`
- `user_roles`
- `content`
- `terms`
- `content_terms`
- `custom_fields`

### ⚠️ Handling Encoding Issues (UTF‑8 / Cyrillic)

If you encounter errors like:
ERROR 1366 (HY000): Incorrect string value: '\xD0\x98\xD0\xBD...' for column 'title'

it means that MySQL created your target table with the wrong default collation (latin1). 
By default MySQL 5.7 uses `latin1` unless explicitly specified.

#### Step 1. Check table encoding
Inside MySQL:
```sql
SHOW CREATE TABLE a264971_dniester.node \G
```

Usually, Drupal 6 tables are DEFAULT CHARSET=utf8.
Now check your new content table (likely has DEFAULT CHARSET=latin1).

Step 2. Recreate the target table with UTF‑8
Drop and re‑create the content table with correct charset:

```sql
DROP TABLE IF EXISTS content;

CREATE TABLE content (
id INT PRIMARY KEY,
title VARCHAR(255),
body TEXT,
teaser TEXT,
publication_date DATETIME,
author_id INT,
FOREIGN KEY (author_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
```
Step 3. Retry data insertion
Now insert data again:

```sql
INSERT INTO content (id, title, body, teaser, publication_date, author_id)
SELECT
n.nid,
n.title,
nr.body,
nr.teaser,
FROM_UNIXTIME(n.created),
n.uid
FROM a264971_dniester.node n
LEFT JOIN a264971_dniester.node_revisions nr ON n.vid = nr.vid;
```
✅ With utf8_general_ci the Cyrillic text will be inserted correctly.



### 3. Export final normalized schema dump
   ```bash
   docker exec -i news-mysql-drupal6 mysqldump -u root -proot dniester > db_data/clean_schema.sql
   ```
### 4. Prepare MySQL 8.0 (News Platform target)
   Important: if MySQL 8.0 was already initialized incorrectly, reset it.

```bash
docker compose -f docker-compose.yml down -v
docker compose -f docker-compose.yml up -d mysql
docker logs news-mysql
```
In logs you should see:

```text
[Entrypoint]: Creating database dniester
[Entrypoint]: Creating user root with password root
```

### 5. Fix root password if needed
   If root is created with empty password/socket auth in MySQL 8.0:

```bash
# Stop container
docker stop news-mysql

# Start temporary container with skip-grant-tables
docker run -it --rm \
--name mysql-fix \
-v news-platform_mysql_data:/var/lib/mysql \
mysql:8.0 \
--skip-grant-tables --skip-networking
```
In another shell:

```bash
docker exec -it mysql-fix mysql
```
Execute inside MySQL:

```sql
FLUSH PRIVILEGES;
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root';
```
Stop mysql-fix (Ctrl+C or docker stop mysql-fix), then restart:

```bash
docker compose -f docker-compose.yml up -d mysql
```
### 6. Import clean schema into MySQL 8.0
   ```bash
   docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
   ```
###7. Verify
   ```bash
# Show all tables
docker exec -it news-mysql mysql -uroot -proot -e "USE dniester; SHOW TABLES;"

# Count number of content rows
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```

Expected: ~12186 rows (12172 story + 14 book).

## ✅ TL;DR Script (All Commands)
### «Short import» / Direct Import into MySQL 8.0 (when clean_schema.sql is ready) 

```bash
# 1. Start MySQL 8.0
docker compose -f docker-compose.yml up -d mysql
docker logs news-mysql

# 2. If root auth issue → reset password manually via skip-grant-tables (described above)

# 3. Import final schema
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql

# 4. Verify
docker exec -it news-mysql mysql -uroot -proot -e "USE dniester; SHOW TABLES;"
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```

### Full Migration Pipeline (Drupal6 → MySQL5.7 → Clean SQL → MySQL8.0) 
```bash
# Dev
docker compose --env-file .env.dev up -d

# Migration. Export from a264971_dniester
docker compose -f docker-compose.drupal.yml up -d
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot a264971_dniester > db_data/drupal6_fixed.sql
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/drupal6_fixed.sql
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/migrate_from_drupal6_universal.sql
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot dniester > db_data/clean_schema.sql

# Import into MySQL 8.0
docker compose -f docker-compose.yml up -d mysql
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```
---

## 📑 Migration SQL Files Overview

During the migration process several SQL scripts have been created and used. Each has a specific purpose:

- **drupal6_fixed.sql**  
  Cleaned snapshot of the original Drupal 6 database (`a264971_dniester`) imported into the temporary MySQL 5.7 instance.  
  Purpose: normalize database name and ensure compatibility for further migration steps.

- **migrate_from_drupal6_universal.sql**  
  Main migration script. It creates a new clean schema (`users`, `roles`, `user_roles`, `content`, `terms`, `content_terms`) and moves normalized data from the old Drupal tables.  
  Core of the migration.

- **detect_custom_fields.sql**  
  Optional helper script. Runs queries against `information_schema` to detect if there are any `content_type_*` tables with additional CCK fields.  
  ⚠️ If this script selects rows → it means you had custom fields.  
  If the result set is empty → you had **no CCK custom fields** in your Drupal 6 dump.

- **migrate_cck_fields.sql**  
  Optional script. Only needed if CCK fields exist. It copies fields from `content_type_*` tables into the generic `custom_fields` table (key → value model).  
  If you had no CCK, this script can be ignored.

- **clean_schema.sql**  
  Final export after applying `migrate_from_drupal6_universal.sql` (and optionally `migrate_cck_fields.sql`).  
  This is the schema and data used in MySQL 8.0 by the News Platform application.

---

### ✅ How to verify CustomFields existence
Run the following in MySQL 5.7 (Drupal container):
```sql
SHOW TABLES LIKE 'content_type%';
```
If you see rows like content_type_article, content_type_news, etc. → you had custom CCK fields.
If nothing is returned → you did not use Drupal CCK, therefore the table custom_fields will remain empty and can be ignored.

### 🔍 How to check CustomFields in the new database only

If the original Drupal 6 container has already been stopped or deleted, you cannot inspect `content_type_*` tables anymore.  
However, you can still verify whether any CCK fields were migrated by checking the `custom_fields` table in your new `dniester` schema.

Run:

```sql
SELECT COUNT(*) FROM custom_fields;
```
If the result is 0 → you had no custom CCK fields or migration of them was skipped.
If the result is greater than 0 → you had custom fields in Drupal 6, and they were migrated into custom_fields as key→value records.


## 🧹 Post‑Migration Cleanup

After completing the migration and successfully importing `clean_schema.sql` into MySQL 8.0, you can clean up the temporary Drupal 6 environment.

### Docker volumes

- ✅ **news-platform_mysql_data** → keep this volume (used by MySQL 8.0: `news-mysql`).
- ❌ **news-platform_mysql_data_drupal6** → can be safely removed (leftover from Drupal 6 migration).

### Options

**Option A — just stop Drupal 6 container (keep volume just in case):**
```bash
docker stop news-mysql-drupal6
```
**Option B — completely remove Drupal 6 container and volume:

```bash
docker compose -f docker-compose.drupal.yml down -v
```
👉 After cleanup, only MySQL 8.0 (news-mysql) and its volume (news-platform_mysql_data) should remain for further work with the News Platform.


