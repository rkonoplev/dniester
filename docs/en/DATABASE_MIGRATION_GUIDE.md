# Database Migration & Setup Guide

This guide covers database initialization, migration scripts, and setup procedures for Phoebe CMS.

## 🚀 Quick Setup

### New Installation (Clean Database)

**Automatic Setup via Spring Boot:**
```bash
# Start MySQL
docker compose up -d

# Run application (auto-applies migrations V1-V6)
cd backend
./gradlew bootRun --args='--spring.profiles.active=local'
```

**Default Admin Credentials:**
- Username: `admin`
- Password: `admin`
- **⚠️ Change immediately in production!**

### Manual Admin User Creation

If you need to create admin user manually:

```bash
mysql phoebe_db < db_data/create_admin_user.sql
```

## 🔄 Migration from Drupal 6

### Migration Workflow

1. **Analysis Phase**
   ```sql
   mysql drupal6_db < db_data/detect_custom_fields.sql
   ```

2. **Main Migration**
   ```sql
   mysql clean_db < db_data/migrate_from_drupal6_universal.sql
   ```

3. **Custom Fields Migration**
   ```sql
   mysql clean_db < db_data/migrate_cck_fields.sql
   ```

4. **User Data Cleanup**
   ```sql
   mysql clean_db < db_data/update_migrated_users.sql
   ```

### Post-Migration Credentials

After migration, users will have:
- **Admin**: username `admin`, password `admin`
- **Migrated users**: their original username, password `changeme123`
- **All migrated users must reset passwords on first login**

## 📁 Migration Scripts Reference

### Core Scripts

#### `migrate_from_drupal6_universal.sql` (3.2K)
- Main migration from Drupal 6 to modern schema
- Creates UTF8 tables and migrates core data
- Unifies all node types into single `content` table

#### `migrate_cck_fields.sql` (1.7K)
- Handles Drupal 6 CCK custom fields
- Preserves field data in normalized format

#### `update_migrated_users.sql` (1.2K)
- Post-migration user cleanup
- Sets temporary passwords requiring reset
- Creates admin user with proper credentials

#### `detect_custom_fields.sql` (212B)
- Discovery script for Drupal 6 CCK fields
- Database introspection for migration planning

#### `create_admin_user.sql`
- Creates default admin user for local development
- **For development only** - password is `admin`

## 🗄️ Database Schema

### Spring Boot Migrations (V1-V6)

The application automatically applies these migrations:

| Migration | Purpose | Changes |
|-----------|---------|---------|
| V1 | Initial schema | Core tables: users, roles, content, terms |
| V2 | Publication workflow | Added `published` column to content |
| V3 | Sample data | **Default admin user and test content** |
| V4 | User unification | Consolidated migrated authors |
| V5 | Permissions system | Added permissions and role_permissions tables |
| V6 | Setup permissions | Populated default permissions and role assignments |

### Migration V3 Default Data

**⚠️ Important**: Migration V3 creates default login credentials for the CMS web interface:

**Roles Created:**
- `ADMIN` - Full system access
- `EDITOR` - Content management access

**Default User Created:**
- Username: `admin`
- Password: `admin` (BCrypt hashed)
- Email: `admin@example.com`
- Role: `ADMIN`

**Test Data:**
- Sample news article
- General category
- Content relationships

## 🔒 Security Considerations

### Password Security
- All passwords stored as **BCrypt hashes** (strength 10-12)
- Default admin password should be changed immediately
- Migrated users must reset passwords on first login

### Production Checklist
- [ ] Change default admin password
- [ ] Force password reset for migrated users
- [ ] Review and adjust role permissions
- [ ] Configure proper database credentials
- [ ] Enable HTTPS
- [ ] Set up backup procedures

## 🛠️ Troubleshooting

### Common Issues

**Migration fails with encoding errors:**
```sql
-- Ensure UTF8 charset
ALTER DATABASE phoebe_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**Admin user already exists:**
```sql
-- Check existing users
SELECT * FROM users WHERE username = 'admin';
-- Update password if needed
UPDATE users SET password = '$2a$12$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jF4l3q4R4J8C' WHERE username = 'admin';
```

**Verify migration success:**
```sql
SELECT COUNT(*) FROM content;   -- Check articles migrated
SELECT COUNT(*) FROM users;     -- Check users migrated
SELECT COUNT(*) FROM terms;     -- Check taxonomy migrated
```

## 📚 Related Documentation

- [Database Schema](./DATABASE_SCHEMA.md) - Complete schema documentation
- [Migration Drupal6 Guide](./MIGRATION_DRUPAL6.md) - Detailed migration process
- [Configuration Guide](./CONFIG_GUIDE.md) - Database configuration
- [Developer Guide](./DEVELOPER_GUIDE.md) - Local development setup