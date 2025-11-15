# MySQL Commands Guide

This document contains useful commands for working with MySQL, both in a Docker container and with a local installation.

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