# Legacy Docker Compose Files

This folder contains Docker Compose configuration files that were used during 
the migration from Drupal 6 to the Spring Boot News Platform.

## Files

### `docker-compose.drupal.yml`
Temporary Docker Compose setup for Drupal 6 data migration. This file:
- Runs MySQL 5.7 for compatibility with legacy Drupal 6 database dumps
- Exposes MySQL on port 3307 to avoid conflicts with the main application database
- Auto-loads database dumps from `./db_dumps` directory
- Mounts migration scripts from `./db_data` directory
- Uses separate volume `mysql_data_drupal6` for isolation

### `docker-compose.override.yml`
Production override configuration for Docker Compose. This file provides:
- Production-ready security with Docker secrets for sensitive data
- Health checks for both database and application services
- Proper service dependencies and restart policies
- Secure database configuration without external port exposure
- Application logging with persistent volumes

## Usage Context

These files were part of the migration process from a legacy Drupal 6 site to the modern Spring Boot News Platform. 
They are preserved here for educational purposes and to help developers understand the migration approach used in this 
study project.

For current development, use the main `docker-compose.yml` in the project root.