# Docker Volume Migration Guide

This guide explains how to safely migrate Docker volumes when renaming containers from `news-mysql` to `phoebe-mysql`.

## Current State
- Container: `news-mysql`
- Volume: `mysql_data` (or `phoebe_mysql_data`)
- Database: `dniester` or `phoebe_db`

## Migration Steps

### Step 1: Create Database Backup
```bash
# Stop current services
docker compose down

# Start only MySQL to create backup
docker compose up -d news-mysql

# Wait for MySQL to be ready
docker exec news-mysql mysqladmin ping -h localhost --silent

# Create backup
docker exec news-mysql mysqldump -uroot -proot --all-databases > backup_before_migration.sql
```

### Step 2: Update docker-compose.yml
```bash
# Update container name in docker-compose.yml
sed -i 's/news-mysql:/phoebe-mysql:/g' docker-compose.yml
sed -i 's/container_name: news-mysql/container_name: phoebe-mysql/g' docker-compose.yml
sed -i 's/news-mysql:/phoebe-mysql:/g' docker-compose.yml
```

### Step 3: Migrate Volume Data
```bash
# Stop old container
docker stop news-mysql
docker rm news-mysql

# Start new container with same volume
docker compose up -d phoebe-mysql

# Wait for MySQL to be ready
docker exec phoebe-mysql mysqladmin ping -h localhost --silent

# Verify data integrity
docker exec phoebe-mysql mysql -uroot -proot -e "SHOW DATABASES;"
```

### Step 4: Update Environment Variables (if needed)
```bash
# Update database name if migrating from dniester to phoebe_db
docker exec -it phoebe-mysql mysql -uroot -proot -e "CREATE DATABASE IF NOT EXISTS phoebe_db;"
docker exec -it phoebe-mysql mysql -uroot -proot -e "RENAME TABLE dniester.* TO phoebe_db.*;" # MySQL 8.0+
# OR use mysqldump approach:
docker exec phoebe-mysql mysqldump -uroot -proot dniester > temp_db.sql
docker exec -i phoebe-mysql mysql -uroot -proot phoebe_db < temp_db.sql
```

### Step 5: Update Application Configuration
Update `docker-compose.yml` database URL:
```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://phoebe-mysql:3306/${MYSQL_DATABASE:-phoebe_db}
```

### Step 6: Test Migration
```bash
# Start all services
docker compose up -d

# Verify application connects
curl http://localhost:8080/health

# Check database content
docker exec phoebe-mysql mysql -uroot -proot -e "SELECT COUNT(*) FROM phoebe_db.content;"
```

## Rollback Plan
If migration fails:
```bash
# Stop new services
docker compose down

# Restore old container name in docker-compose.yml
git checkout docker-compose.yml

# Restore from backup
docker compose up -d news-mysql
docker exec -i news-mysql mysql -uroot -proot < backup_before_migration.sql
```

## Automated Migration Scripts

For convenience, use the provided scripts:

```bash
# Run migration (creates backup in db_dumps/)
./migrate_volumes.sh

# If something goes wrong, rollback
./rollback_migration.sh
```

## Clean Up
After successful migration:
```bash
# Remove backup files (optional)
rm db_dumps/backup_before_migration_*.sql

# Remove docker-compose backup
rm docker-compose.yml.bak
```