# Legacy Docker Compose Files

This folder contains Docker Compose configuration files that were used during 
the migration from Drupal 6 to the Spring Boot News Platform.

## Files

### `DatabaseProperties.java`
Legacy Spring Boot configuration class used during Drupal 6 migration. This file:
- **Original location**: `backend/src/main/java/com/example/newsplatform/config/DatabaseProperties.java`
- Defined custom database connection pool settings for migration workload
- Provided extended connection timeouts for large data transfers
- Enabled SQL logging for debugging migration queries
- Used `@ConfigurationProperties(prefix = "app.database")` for configuration binding
- **Status**: OBSOLETE - Migration completed, standard Spring datasource config used

### `Makefile`
Legacy testing utility with API endpoint shortcuts. This file:
- **Original location**: Project root directory (`/Makefile`)
- Provided `make test-news-get`, `make test-news-post` commands for API testing
- Used outdated API endpoints (`/api/news` instead of `/api/public/news`)
- Required manual credential setup via environment variables
- **Status**: OBSOLETE - Replaced by curl examples in API_USAGE.md and rate limiting



### `docker-compose.drupal.yml`
Temporary Docker Compose setup for Drupal 6 data migration. This file:
- **Original location**: Project root directory (`/docker-compose.drupal.yml`)
- Runs MySQL 5.7 for compatibility with legacy Drupal 6 database dumps
- Exposes MySQL on port 3307 to avoid conflicts with the main application database
- Auto-loads database dumps from `./db_dumps` directory
- Mounts migration scripts from `./db_data` directory
- Uses separate volume `mysql_data_drupal6` for isolation

### `docker-compose.override.yml`
Production override configuration for Docker Compose. This file provides:
- **Original location**: Project root directory (`/docker-compose.override.yml`)
- Production-ready security with Docker secrets for sensitive data
- Health checks for both database and application services
- Proper service dependencies and restart policies
- Secure database configuration without external port exposure
- Application logging with persistent volumes

### `ExampleTest.java`
Legacy placeholder test file from early development. This file:
- **Original location**: `backend/src/test/java/com/example/newsplatform/ExampleTest.java`
- Simple JUnit 5 test with `assertTrue(true)` assertion
- Created as initial test structure during project setup
- **Status**: OBSOLETE - Replaced by comprehensive unit and integration tests

## Usage Context

## Migration Context

These files were part of the migration and early development process from a legacy Drupal 6 site to the modern Spring Boot News Platform. They include:

- **Migration-specific configurations** (Docker Compose files for Drupal 6 compatibility)
- **Legacy development tools** (Makefile with outdated API endpoints)

- **Migration-era Spring Boot classes** (custom database properties)

They are preserved here for educational purposes and to help developers understand the migration approach and evolution of the project.

## Current Development

For current development, use:
- Main `docker-compose.yml` in the project root
- API examples in `docs/API_USAGE.md`
- Standard Spring Boot configuration profiles