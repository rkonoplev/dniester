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
At this point you can run your migration SQL scripts to normalize DB:

```sql
source db_data/migrate_from_drupal6_universal.sql;
source db_data/migrate_cck_fields.sql; -- optional
```
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

# Migration
docker compose -f docker-compose.drupal.yml up -d
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot a264971_dniester > db_data/drupal6_fixed.sql
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/drupal6_fixed.sql
docker exec -i news-mysql-drupal6 mysql -uroot -proot dniester < db_data/migrate_from_drupal6_universal.sql
docker exec -i news-mysql-drupal6 mysqldump -uroot -proot dniester > db_data/clean_schema.sql

docker compose -f docker-compose.yml up -d mysql
docker exec -i news-mysql mysql -uroot -proot dniester < db_data/clean_schema.sql
docker exec -it news-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM content;" dniester
```


