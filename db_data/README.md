# Database Migration Scripts

This directory contains SQL scripts and database files used for migrating from Drupal 6 to the modern News Platform schema.

## 📁 Files Overview

### Development & Initialization Scripts (Safe for Git)

#### `init_admin.sql` (NEW)
- **Purpose**: Creates a default admin user for **local development from a clean database**.
- **Created**: For developer onboarding and clean-slate scenarios.
- **Function**:
    - Ensures the `ADMIN` role exists.
    - Creates a user `admin` with a configurable password (BCrypt hash).
    - Assigns the `ADMIN` role to the user.
- **Usage**: Run this script after starting a fresh MySQL instance to get immediate admin access.
- **Security**: **For local development ONLY**. Contains a placeholder password hash that must be replaced before use.

#### `create_admin_user.sql` (LEGACY / ALTERNATE)
- **Purpose**: An alternative script for creating an admin user, often used during initial project setup or migration verification.
- **Status**: Functionally similar to `init_admin.sql` but may contain different default values or comments.
- **Usage**: Can be used interchangeably with `init_admin.sql` for local development setup.
- **Note**: Prefer `init_admin.sql` for new setups as it is the standardized script.

### Migration Scripts (Safe for Git)

#### `migrate_from_drupal6_universal.sql` (3.2K)
- **Purpose**: Main migration script from Drupal 6 to clean modern schema
- **Created**: During migration process (August 2024)
- **Function**: 
  - Creates new UTF8 tables (users, roles, content, terms, custom_fields)
  - Migrates core data from Drupal 6 tables
  - Establishes foreign key relationships
  - Unifies all node types into single `content` table
- **Usage**: Run after importing Drupal 6 data into temporary database
- **Technical Value**: Shows complete schema transformation approach

#### `migrate_cck_fields.sql` (1.7K)
- **Purpose**: Handles Drupal 6 CCK (Content Construction Kit) custom fields
- **Created**: For handling complex Drupal field migrations
- **Function**:
  - Scans `content_type_*` tables for custom fields
  - Generates INSERT statements for custom_fields table
  - Preserves field data in normalized format
- **Usage**: Run after main migration to handle custom content fields
- **Technical Value**: Demonstrates dynamic SQL generation for field migration

#### `detect_custom_fields.sql` (212B)
- **Purpose**: Discovery script for Drupal 6 CCK fields
- **Created**: For analysis phase of migration
- **Function**:
  - Lists all `content_type_*` tables
  - Shows column structure of custom content types
- **Usage**: Run on Drupal 6 database to understand field structure
- **Technical Value**: Shows database introspection techniques

### Data Files (Excluded from Git)

#### `clean_schema.sql` (289M) - **Not in Git**
- **Purpose**: Final clean database with migrated content
- **Size**: Too large for Git (289MB)
- **Content**: Complete migrated database with actual news articles
- **Status**: Generated output from migration process
- **Location**: Local development only

#### `drupal6_fixed.sql` (225M) - **Not in Git**
- **Purpose**: Original Drupal 6 database export with fixes
- **Size**: Too large for Git (225MB)
- **Content**: Legacy Drupal 6 data with character encoding fixes
- **Status**: Historical reference, migration source
- **Location**: Local development only

## 🔄 Migration Workflow

The migration process follows this sequence:

1. **Analysis Phase**
   ```sql
   -- Run detect_custom_fields.sql on Drupal 6 DB
   mysql drupal6_db < detect_custom_fields.sql
   ```

2. **Main Migration**
   ```sql
   -- Run universal migration script
   mysql clean_db < migrate_from_drupal6_universal.sql
   ```

3. **Custom Fields Migration**
   ```sql
   -- Handle CCK fields separately
   mysql clean_db < migrate_cck_fields.sql
   ```

4. **Verification**
   ```sql
   -- Verify data integrity and counts
   SELECT COUNT(*) FROM content;
   SELECT COUNT(*) FROM users;
   SELECT COUNT(*) FROM terms;
   ```

## 🔧 Technical Implementation

These scripts demonstrate:

- **Legacy System Migration**: Complete CMS migration approach
- **Schema Modernization**: Drupal 6 → MySQL 8.0 transformation
- **Data Normalization**: Converting Drupal's EAV model to relational
- **UTF8 Migration**: Character encoding fixes and standardization
- **Foreign Key Design**: Modern relational database constraints
- **Dynamic SQL**: Programmatic migration script generation

## 🔒 Privacy & Security

- **No Personal Data**: Large database files excluded from public repository
- **Migration Scripts Only**: Only migration logic shared publicly
- **Sanitized Examples**: Sample field names and structures shown
- **No Credentials**: No database passwords or connection strings included

## 📚 Related Documentation

- [Complete Migration Guide](../docs/MIGRATION_DRUPAL6.md) - Full migration process
- [Database Schema](../docs/DATABASE_SCHEMA.md) - Final schema documentation
- [Developer Guide](../docs/DEVELOPER_GUIDE.md) - Local development setup

## ⚠️ Usage Notes

- Scripts reference `a264971_dniester` database - update for your environment
- Large data files must be obtained separately for local development
- Migration scripts are reference examples, adapt for production use
- Always backup databases before running migration scripts