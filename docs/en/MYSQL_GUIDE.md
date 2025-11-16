> [Back to Documentation Contents](./README.md) | [Database Guide](./DATABASE_GUIDE.md)

# MySQL Commands Guide

This document contains useful commands for working with MySQL, both in a Docker container and with a local installation.

## Table of Contents
- [Working with MySQL in a Docker Container](#working-with-mysql-in-a-docker-container)
- [Working with a Local MySQL Installation](#working-with-a-local-mysql-installation)
- [Data Manipulation in DB](#data-manipulation-in-db)
  - [1. Modifying Data in Specific Cells](#1-modifying-data-in-specific-cells)
  - [2. Mass Correction of Image Paths and HTML Tag Removal](#2-mass-correction-of-image-paths-and-html-tag-removal)
    - [Scenario 1: Moving Images to Local Project Folders](#scenario-1-moving-images-to-local-project-folders)
    - [Scenario 2: Moving Images to External Servers](#scenario-2-moving-images-to-external-servers)
  - [3. Managing CMS User Accounts (Admin/Editor)](#3-managing-cms-user-accounts-admineditor)
    - [Changing Password for an Existing User](#changing-password-for-an-existing-user)
    - [Creating a New User (Admin/Editor)](#creating-a-new-user-admineditor)

---

## Working with MySQL in a Docker Container

It is assumed that your MySQL container is running and named `phoebe-mysql`.

### Connecting in interactive mode:

```bash
docker exec -it phoebe-mysql mysql -uroot -proot
```

### Inside MySQL (after connecting, `mysql>` prompt appears):

**View all databases:**
```sql
SHOW DATABASES;
```

**Switch to a database:**
```sql
USE phoebe_db;
```

**View tables in the current database:**
```sql
SHOW TABLES;
```

**Example: count records in the `content` table:**
```sql
SELECT COUNT(*) FROM content;
```

**Exit MySQL client:**
```sql
EXIT;
```

### Exporting data (creating a DB dump):

**Dump the entire `phoebe_db` database:**
```bash
docker exec -i phoebe-mysql mysqldump -uroot -proot phoebe_db > db_data/exported_dump.sql
```

**Dump a specific table (e.g., `users`):**
```bash
docker exec -i phoebe-mysql mysqldump -uroot -proot phoebe_db users > db_data/users_dump.sql
```

### Importing data (loading a dump into the DB):

```bash
docker exec -i phoebe-mysql mysql -uroot -proot phoebe_db < db_data/exported_dump.sql
```

### Notes:

-   In export/import commands, specify the database name (e.g., `phoebe_db`).
-   The database must exist before importing.
-   A dump is a regular `.sql` file; it can be stored in the `db_data` folder for convenience.

---

## Working with a Local MySQL Installation

If MySQL is installed directly on your machine, the commands will be similar but without the `docker exec` prefix.
It is assumed that the MySQL server is running, and you know the username (`-u`) and password (`-p`).

### Connecting in interactive mode:

```bash
mysql -u <username> -p
# Example: mysql -u root -p
```
After entering the command, the system will prompt for the password.

### Inside MySQL (after connecting, `mysql>` prompt appears):

The commands inside the MySQL client are identical to those used in the Docker container:

**View all databases:**
```sql
SHOW DATABASES;
```

**Switch to a database:**
```sql
USE phoebe_db;
```

**View tables in the current database:**
```sql
SHOW TABLES;
```

**Example: count records in the `content` table:**
```sql
SELECT COUNT(*) FROM content;
```

**Exit MySQL client:**
```sql
EXIT;
```

### Exporting data (creating a DB dump):

**Dump the entire `phoebe_db` database:**
```bash
mysqldump -u <username> -p phoebe_db > db_data/exported_dump.sql
# Example: mysqldump -u root -p phoebe_db > db_data/exported_dump.sql
```

**Dump a specific table (e.g., `users`):**
```bash
mysqldump -u <username> -p phoebe_db users > db_data/users_dump.sql
# Example: mysqldump -u root -p phoebe_db users > db_data/users_dump.sql
```

### Importing data (loading a dump into the DB):

```bash
mysql -u <username> -p phoebe_db < db_data/exported_dump.sql
# Example: mysql -u root -p phoebe_db < db_data/exported_dump.sql
```

---

## Data Manipulation in DB

This section describes SQL commands for modifying data in the database, including specific cell corrections, mass updates, and managing user accounts.

### 1. Modifying Data in Specific Cells

To change the value in one or more specific cells, use the `UPDATE` command with a `WHERE` clause.

**Example: Change the title of an article with `id = 1`:**
```sql
UPDATE content
SET title = 'New Article Title'
WHERE id = 1;
```

**Example: Change the publication status of an article with `id = 5`:**
```sql
UPDATE content
SET published = TRUE
WHERE id = 5;
```

**Example: Change the email of the `admin` user:**
```sql
UPDATE users
SET email = 'new_admin@example.com'
WHERE username = 'admin';
```

### 2. Mass Correction of Image Paths and HTML Tag Removal

Often after migration or file structure reorganization, it is necessary to update image paths or clean content from outdated HTML tags.

#### Scenario 1: Moving Images to Local Project Folders

If images are now stored in folders within your project (e.g., `/images/uploads/`), and you need to update paths in `<img>` tags within `body` or `teaser` fields.

**Example: Changing the `src` path in `<img>` tags:**
Suppose the old path was `http://old-domain.com/sites/default/files/` and it needs to be replaced with `/images/uploads/`.

```sql
UPDATE content
SET
    body = REPLACE(body, 'http://old-domain.com/sites/default/files/', '/images/uploads/'),
    teaser = REPLACE(teaser, 'http://old-domain.com/sites/default/files/', '/images/uploads/')
WHERE
    body LIKE '%http://old-domain.com/sites/default/files/%' OR
    teaser LIKE '%http://old-domain.com/sites/default/files/%';
```
*   **Important**: The `REPLACE` command is case-sensitive. Ensure you are replacing the exact string.

**Example: Removing specific HTML tags (e.g., `<img>`):**
If you want to completely remove all `<img>` tags from the content, this is more complex to do with pure SQL, as MySQL does not have built-in regex replacement support (only for searching via `REGEXP`). For such tasks, application-level scripts (PHP, Python, Java) or more powerful DBMS with `REGEXP_REPLACE` support are usually used.

However, if tags have a predictable structure, you can use a combination of `REPLACE` to remove known parts. This is not ideal but can help in simple cases.

```sql
-- Example: Removing a specific <img> tag with a known src
UPDATE content
SET
    body = REPLACE(body, '<img src="/old/path/image.jpg">', ''),
    teaser = REPLACE(teaser, '<img src="/old/path/image.jpg">', '')
WHERE
    body LIKE '%<img src="/old/path/image.jpg">%' OR
    teaser LIKE '%<img src="/old/path/image.jpg">%';
```
*   **For complex cases with HTML tags, it is recommended to use application-level tools** that can parse HTML and safely modify it.

#### Scenario 2: Moving Images to External Servers

If images are moved to an external CDN or file storage (e.g., S3, Cloudinary), you need to update the paths to new URLs.

**Example: Changing the `src` path to a new CDN domain:**
Suppose the old path was `/images/uploads/` and it needs to be replaced with `https://cdn.new-domain.com/assets/`.

```sql
UPDATE content
SET
    body = REPLACE(body, '/images/uploads/', 'https://cdn.new-domain.com/assets/'),
    teaser = REPLACE(teaser, '/images/uploads/', 'https://cdn.new-domain.com/assets/')
WHERE
    body LIKE '%/images/uploads/%' OR
    teaser LIKE '%/images/uploads/%';
```

### 3. Managing CMS User Accounts (Admin/Editor)

In case of a lost password or the need to create a new admin/editor account externally (e.g., via direct DB access), the following commands can be used.

**Important**: Passwords in Phoebe CMS are stored as BCrypt hashes. To set a new password, you will need to generate its hash.

#### Changing Password for an Existing User

1.  **Generate a BCrypt hash for the new password.**
    You can use an online BCrypt hash generator or a function in your application. For example, for the password `new_secret_password`, the hash might look like this:
    `$2a$12$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jF4l3q4R4J8C` (this is an example, your hash will be different).

2.  **Update the password in the `users` table:**
    ```sql
    UPDATE users
    SET password = '$2a$12$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jF4l3q4R4J8C' -- Replace with your generated hash
    WHERE username = 'admin'; -- Or 'editor', or another username
    ```

#### Creating a New User (Admin/Editor)

To create a new user, you will need to add a record to the `users` table and link it to the appropriate role in `user_roles`.

1.  **Generate a BCrypt hash for the new password** (see above).

2.  **Add the new user to the `users` table:**
    ```sql
    INSERT INTO users (username, password, email, active)
    VALUES ('new_admin_user', '$2a$12$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jF4l3q4R4J8C', 'new_admin@example.com', TRUE);
    ```

3.  **Get the `id` of the newly created user:**
    ```sql
    SELECT id FROM users WHERE username = 'new_admin_user';
    -- Assume id = 10
    ```

4.  **Get the `id` of the desired role (e.g., `ADMIN` or `EDITOR`):**
    ```sql
    SELECT id FROM roles WHERE name = 'ADMIN';
    -- Assume id = 1
    ```

5.  **Link the user to the role in the `user_roles` table:**
    ```sql
    INSERT INTO user_roles (user_id, role_id)
    VALUES (10, 1); -- Replace 10 with user id, 1 with role id
    ```
    Now `new_admin_user` will have the `ADMIN` role.

---