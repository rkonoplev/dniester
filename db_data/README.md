# Database Migration Scripts

This directory contains SQL scripts for database setup and migration from Drupal 6 to Phoebe CMS.

> **📖 Complete Guide**: See [../docs/en/DATABASE_MIGRATION_GUIDE.md](../docs/en/DATABASE_MIGRATION_GUIDE.md)  
> for full setup instructions.

## 📁 Files Overview

### Setup Scripts
- `create_admin_user.sql` - Creates admin user for local development (password: `admin`)

### Migration Scripts  
- `migrate_from_drupal6_universal.sql` - Main Drupal 6 migration
- `migrate_cck_fields.sql` - Custom fields migration
- `update_migrated_users.sql` - User cleanup (sets password: `changeme123`)
- `detect_custom_fields.sql` - Field discovery script

### Data Files (Not in Git)
- `clean_schema.sql` (289M) - Complete migrated database
- `drupal6_fixed.sql` (225M) - Original Drupal 6 data

**⚠️ Important**: These create **CMS login credentials**, not database passwords:
- **Spring Boot Migration V3**: Creates `admin` user with password `admin`
- **Migration Scripts**: Set migrated users to password `changeme123`

## 🚀 Quick Usage

**New Installation:**
```bash
# Automatic setup via Spring Boot
docker compose up -d
cd backend && ./gradlew bootRun --args='--spring.profiles.active=local'
# Login: admin/admin
```

**Manual Admin Setup:**
```bash
mysql phoebe_db < create_admin_user.sql
```

**Drupal 6 Migration:**
```bash
mysql clean_db < migrate_from_drupal6_universal.sql
mysql clean_db < update_migrated_users.sql
# Login: admin/admin or username/changeme123
```