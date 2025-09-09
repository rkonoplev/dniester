# Database Dumps

This directory contains database dump files used for migration and development purposes.

## Files

### `drupal6_working.sql`
- **Source**: Original Drupal 6 database export
- **Purpose**: Starting point for migration from legacy Drupal 6 system to News Platform
- **Usage**: Imported into temporary MySQL 5.7 container during migration process
- **Status**: Historical reference - migration completed

## Migration Process

These dumps were used in the migration workflow:

1. **drupal6_working.sql** → Import into MySQL 5.7 container
2. Apply normalization scripts from `../db_data/`
3. Export clean schema → `../db_data/clean_schema.sql`
4. Import final schema into MySQL 8.0

For complete migration instructions, see [Migration Guide](../docs/MIGRATION_DRUPAL6.md).

## Note

This directory is referenced in Docker Compose configurations for automatic database initialization during development setup.