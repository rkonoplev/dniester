# Database Guide – Phoebe CMS

This comprehensive guide covers database schema, setup, migration, and troubleshooting for Phoebe CMS.  
The schema supports both **clean installations** and **migrated data from Drupal 6**.

## Table of Contents
- [Database Schema](#database-schema)
- [Quick Setup](#quick-setup)
- [Migration from Drupal 6](#migration-from-drupal-6)
- [Migration Scripts Reference](#migration-scripts-reference)
- [Spring Boot Migrations](#spring-boot-migrations)
- [Authentication & Access Control](#authentication--access-control)
- [Troubleshooting](#troubleshooting)
- [Related Documentation](#related-documentation)

---

## Database Schema

### Schema Features

- **User Management**: Authentication with role-based access control
- **Permissions System**: Granular permissions assigned to roles
- **Content Management**: Unified storage for news articles and content
- **Taxonomy System**: Categories and tags with flexible vocabularies
- **Publication Workflow**: Draft/published states with audit trails
- **Migration Support**: Handles legacy Drupal 6 data transformation

### Current Database Schema (After All Migrations V1-V9)

```sql
-- ======================================
-- USERS TABLE
-- ======================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,              -- BCrypt hashed passwords
    email VARCHAR(255) UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE         -- true = active, false = blocked
);

-- ======================================
-- ROLES TABLE
-- ======================================
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,            -- 'ADMIN', 'EDITOR', etc.
    description VARCHAR(255)                     -- Role description (added in V5)
);

-- ======================================
-- PERMISSIONS TABLE (Added in V5)
-- ======================================
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE            -- 'news:read', 'users:create', etc.
);

-- ======================================
-- USER_ROLES (Many-to-Many)
-- ======================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ======================================
-- ROLE_PERMISSIONS (Many-to-Many, Added in V5)
-- ======================================
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- ======================================
-- CONTENT TABLE (News Articles)
-- ======================================
CREATE TABLE content (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body TEXT,                                   -- Full article content
    teaser TEXT,                                 -- Article summary/excerpt
    publication_date DATETIME NOT NULL,
    published BOOLEAN NOT NULL DEFAULT FALSE,    -- Added in V2: draft/published state
    created_at DATETIME,                         -- Audit: creation timestamp
    updated_at DATETIME,                         -- Audit: last modification
    version BIGINT,                              -- Optimistic locking
    author_id BIGINT NOT NULL,                   -- FK to users.id
    FOREIGN KEY (author_id) REFERENCES users(id)
);

-- ======================================
-- TERMS TABLE (Taxonomy)
-- ======================================
CREATE TABLE terms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    vocabulary VARCHAR(100),                     -- 'category', 'tag', etc.
    UNIQUE (name, vocabulary)
);

-- ======================================
-- CONTENT_TERMS (Many-to-Many)
-- ======================================
CREATE TABLE content_terms (
    content_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    PRIMARY KEY (content_id, term_id),
    FOREIGN KEY (content_id) REFERENCES content(id) ON DELETE CASCADE,
    FOREIGN KEY (term_id) REFERENCES terms(id) ON DELETE CASCADE
);
```

### Example Queries

```sql
-- List all published articles with authors
SELECT c.title, c.publication_date, u.username as author
FROM content c
JOIN users u ON c.author_id = u.id
WHERE c.published = TRUE
ORDER BY c.publication_date DESC;

-- Get user permissions through roles
SELECT u.username, p.name as permission
FROM users u
JOIN user_roles ur ON u.id = ur.user_id
JOIN roles r ON ur.role_id = r.id
JOIN role_permissions rp ON r.id = rp.role_id
JOIN permissions p ON rp.permission_id = p.id
WHERE u.active = TRUE;

-- Find articles by term
SELECT c.title, t.name as term
FROM content c
JOIN content_terms ct ON c.id = ct.content_id
JOIN terms t ON ct.term_id = t.id
WHERE t.name = 'Technology';
```

---

## Quick Setup

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

---

## Migration from Drupal 6

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

### Archive Data & Vocabulary System

**Important:** All archive terms from Drupal 6 are fully preserved with their vocabulary classifications.

**Migration ensures:**
- ✅ **Archive integrity**: All existing news-term relationships maintained
- ✅ **Vocabulary preservation**: Original Drupal 6 vocabulary names ("category", "tags", etc.) kept
- ✅ **No data loss**: Complete taxonomy structure migrated
- ✅ **Future flexibility**: New terms can use existing or new vocabulary groupings

---

## Migration Scripts Reference

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

---

## Spring Boot Migrations

### Automatic Migrations (Flyway)

The application uses Flyway to automatically manage schema evolution. To support multiple database systems, scripts
are organized into common and vendor-specific directories.

- `db/migration/common`: Scripts compatible with all supported databases.
- `db/migration/mysql`: Scripts for MySQL only.
- `db/migration/postgresql`: Scripts for PostgreSQL only.

Flyway's locations are configured via Spring profiles, allowing it to combine common and DB-specific migrations.

| Migration | Purpose | Changes | Location |
|-----------|---------|---------|----------|
| V1 | Initial schema | Core tables: users, roles, content, terms | `common` |
| V3 | Sample data | **Default admin user and test content** | `common` |
| V4 | User unification | Consolidated migrated authors | `common` |
| V5 | Permissions system | Added permissions and role_permissions tables | `common` |
| V6 | Permission descriptions | Added description column to permissions table | `common` |
| V7 | Channel settings | Added channel_settings table for site configuration | `common` |
| V8 | Setup permissions | Populate permissions and assign to roles | `mysql/postgresql` |
| V9 | Add indexes | Performance indexes and unique constraints | `mysql/postgresql` |

### Migration V3 Default Data

**⚠️ Important**: Migration V3 creates default login credentials for the CMS web interface:

**Roles Created:**
- `ADMIN` - Full system access
- `EDITOR` - Content management access

**Default User Created:**
- Username: `admin`
- Password: `admin` (BCrypt hash)
- Email: `admin@example.com`
- Role: `ADMIN`

**Test Data:**
- Sample news article
- General category
- Content relationshipsCrypt hashed)
- Email: `admin@example.com`
- Role: `ADMIN`

**Test Data:**
- Sample news article
- General category
- Content relationships

---

## Authentication & Access Control

### Default Users & Passwords

**⚠️ Important**: These are passwords for logging into the website (CMS web interface), not MySQL database 
passwords.

#### For Clean Database (New Installation)
After running migrations V1-V6 on a fresh database:

| Username | Password | Role | Email | Purpose |
|----------|----------|------|-------|---------|
| `admin` | `admin` | ADMIN | admin@example.com | System administrator |

#### For Migrated Database (From Drupal 6)
After running migration scripts + `update_migrated_users.sql`:

| Username | Password | Role | Email | Purpose |
|----------|----------|------|-------|---------|
| `admin` | `admin` | ADMIN | admin@phoebe.local | System administrator |
| All migrated users | `changeme123` | - | user{id}@migrated.local | Legacy users (must reset) |

### Password Security
- All passwords are stored as **BCrypt hashes** (strength 10-12)
- Migrated users **must change password** on first login
- Admin password should be changed immediately in production

### Permission System

The system uses **resource:action** permission naming:

| Permission | Description |
|------------|-------------|
| `news:read` | View news articles |
| `news:create` | Create new articles |
| `news:update` | Edit existing articles |
| `news:delete` | Delete articles |
| `news:publish` | Publish/unpublish articles |
| `users:read` | View user accounts |
| `users:create` | Create new users |
| `users:update` | Edit user accounts |
| `users:delete` | Delete users |
| `roles:*` | Role management permissions |
| `terms:*` | Taxonomy management permissions |

### Default Role Permissions

**ADMIN Role:**
- All permissions (full system access)

**EDITOR Role:**
- `news:read`, `news:create`, `news:update`, `news:publish`
- `terms:read`

---

## Troubleshooting

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
UPDATE users SET password = '$2a$12$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P8jF4l3q4R4J8C' 
WHERE username = 'admin';
```

**Verify migration success:**
```sql
SELECT COUNT(*) FROM content;   -- Check articles migrated
SELECT COUNT(*) FROM users;     -- Check users migrated
SELECT COUNT(*) FROM terms;     -- Check taxonomy migrated
```

### Database Setup Instructions

#### For New Installation (Clean Database)

1. **Start MySQL 8.0**:
   ```bash
   docker compose up -d
   ```

2. **Run Spring Boot** (auto-applies migrations V1-V6):
   ```bash
   cd backend
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

3. **First Login**:
   - Username: `admin`
   - Password: `admin`
   - **⚠️ Change password immediately!**

#### For Migrated Database (From Drupal 6)

1. **Import Drupal 6 data** (if available)
2. **Run migration scripts**:
   ```bash
   mysql phoebe_db < db_data/migrate_from_drupal6_universal.sql
   mysql phoebe_db < db_data/update_migrated_users.sql
   ```

3. **Start application** (applies remaining migrations)
4. **First Login Options**:
   - Admin: username `admin`, password `admin`
   - Migrated users: their original username, password `changeme123`

### Security Checklist

- [ ] Change default admin password
- [ ] Force password reset for all migrated users
- [ ] Review and adjust role permissions
- [ ] Enable HTTPS in production
- [ ] Configure proper database credentials
- [ ] Set up backup procedures

---

## Related Documentation

- [Project Overview](./PROJECT_OVERVIEW.md) - Complete project information
- [Migration Drupal6 Guide](./MIGRATION_DRUPAL6.md) - Detailed migration process
- [Configuration Guide](./CONFIG_GUIDE.md) - Database configuration
- [Developer Guide](./DEVELOPER_GUIDE.md) - Local development setup