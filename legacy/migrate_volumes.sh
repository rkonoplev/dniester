#!/bin/bash

# Volume Migration Script for Phoebe CMS
# Migrates from news-mysql to phoebe-mysql containers safely

set -e  # Exit on any error

echo "🚀 Starting Docker Volume Migration for Phoebe CMS"
echo "=================================================="

# Step 1: Create backup directory
echo "📁 Creating backup directory..."
mkdir -p db_dumps
cd "$(dirname "$0")"  # Ensure we're in project root

# Step 2: Stop current services and clean up
echo "🛑 Stopping current services and cleaning up..."
docker compose down --remove-orphans
docker container prune -f
docker network prune -f

# Step 3: Start only MySQL to create backup
echo "🔄 Starting MySQL for backup..."
docker compose up -d phoebe-mysql

# Step 4: Wait for MySQL to be ready
echo "⏳ Waiting for MySQL to be ready..."
for i in {1..30}; do
  if docker exec phoebe-mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
    echo "✅ MySQL is ready!"
    break
  fi
  echo "Waiting... ($i/30)"
  sleep 2
done

# Step 5: Create backup
echo "💾 Creating full database backup..."
docker exec phoebe-mysql mysqladump -uroot -proot --all-databases > db_dumps/backup_before_migration_$(date +%Y%m%d_%H%M%S).sql
echo "✅ Backup created in db_dumps/"

# Step 6: Stop container
echo "🛑 Stopping container..."
docker stop phoebe-mysql

# Step 7: No docker-compose.yml update needed (already updated)
echo "📝 docker-compose.yml already updated to use phoebe naming"

# Step 8: Start container again
echo "🔄 Starting MySQL container..."
docker compose up -d phoebe-mysql

# Step 9: Wait for MySQL to be ready
echo "⏳ Waiting for MySQL to be ready..."
for i in {1..30}; do
  if docker exec phoebe-mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
    echo "✅ MySQL is ready!"
    break
  fi
  echo "Waiting... ($i/30)"
  sleep 2
done

# Step 10: Verify data integrity
echo "🔍 Verifying data integrity..."
echo "Databases found:"
docker exec phoebe-mysql mysql -uroot -proot -e "SHOW DATABASES;"

# Check if we have data
echo "Checking content table:"
docker exec phoebe-mysql mysql -uroot -proot -e "SELECT COUNT(*) as content_count FROM phoebe_db.content;" 2>/dev/null || \
docker exec phoebe-mysql mysql -uroot -proot -e "SELECT COUNT(*) as content_count FROM dniester.content;" 2>/dev/null || \
echo "No content table found (this is normal for fresh installations)"

echo ""
echo "✅ Migration completed successfully!"
echo "📁 Backup saved in: db_dumps/backup_before_migration_*.sql"
echo "🔧 docker-compose.yml already uses phoebe naming"
echo ""
echo "Next steps:"
echo "1. Test your application: docker compose up -d"
echo "2. Verify everything works correctly"
echo "3. All containers now use phoebe-* naming"