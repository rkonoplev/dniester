#!/bin/bash

# Rollback Script for Volume Migration
# Restores original configuration if migration fails

set -e  # Exit on any error

echo "🔄 Rolling back Docker Volume Migration"
echo "======================================"

# Step 1: Stop current services
echo "🛑 Stopping current services..."
docker compose down

# Step 2: Restore original docker-compose.yml
echo "📝 Restoring original docker-compose.yml..."
if [ -f "docker-compose.yml.bak" ]; then
    mv docker-compose.yml.bak docker-compose.yml
    echo "✅ docker-compose.yml restored"
else
    echo "⚠️  No backup found, using git checkout..."
    git checkout docker-compose.yml
fi

# Step 3: Remove new container if exists
echo "🗑️  Removing new container..."
docker rm phoebe-mysql 2>/dev/null || echo "Container phoebe-mysql not found"

# Step 4: Start original services
echo "🔄 Starting original services..."
docker compose up -d news-mysql

# Step 5: Wait for MySQL to be ready
echo "⏳ Waiting for MySQL to be ready..."
for i in {1..30}; do
  if docker exec news-mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
    echo "✅ MySQL is ready!"
    break
  fi
  echo "Waiting... ($i/30)"
  sleep 2
done

# Step 6: Find and restore from backup if needed
echo "🔍 Looking for backup files..."
LATEST_BACKUP=$(ls -t db_dumps/backup_before_migration_*.sql 2>/dev/null | head -n1)

if [ -n "$LATEST_BACKUP" ]; then
    echo "📥 Found backup: $LATEST_BACKUP"
    echo "Do you want to restore from backup? (y/N)"
    read -r response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        echo "📥 Restoring from backup..."
        docker exec -i news-mysql mysql -uroot -proot < "$LATEST_BACKUP"
        echo "✅ Database restored from backup"
    fi
else
    echo "⚠️  No backup files found in db_dumps/"
fi

# Step 7: Verify rollback
echo "🔍 Verifying rollback..."
docker exec news-mysql mysql -uroot -proot -e "SHOW DATABASES;"

echo ""
echo "✅ Rollback completed!"
echo "🔧 Original configuration restored"
echo "📁 Backup files preserved in db_dumps/"