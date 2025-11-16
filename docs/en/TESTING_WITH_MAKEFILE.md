# Testing and Development with Makefile

This guide explains how to use the Makefile commands for development and testing workflows.

## Quick Reference

| Command | Description | Use Case |
|:---|:---|:---|
| `make run` | Start full project (Docker Compose) | Manual testing, frontend development |
| `make stop` | Stop project | Clean shutdown |
| `make rebuild` | Rebuild backend without cache and start | Fixing build cache issues |
| `make hard-rebuild` | Stop, rebuild backend without cache, start | When `rebuild` is not enough |
| `make reset` | **Delete all containers and DB data** | Complete environment reset |
| `make test` | Run integration tests (Testcontainers) | Quick test feedback |
| `make all-tests` | Run all tests (unit + integration) | Full validation |
| `make boot` | Start backend locally | Development with local MySQL |
| `make clean` | Clean build artifacts | Fresh start |
| `make lint` | Run static analysis | Code quality checks |
| `make coverage` | Generate test coverage | Coverage reports |

## Development Workflows

### 1. Full Stack Development
For developing both frontend and backend with live reload:

```bash
# Start everything (MySQL + backend + frontend)
make run
```

**Access Services:**
- API: [http://localhost:8080](http://localhost:8080)
- Swagger: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- Frontend: [http://localhost:3000](http://localhost:3000) (if available)

> **Hint**: Login credentials (usernames and passwords) for different roles are described
> in the **[DATABASE_GUIDE.md](./DATABASE_GUIDE.md)** file.

```bash
# Stop when done
make stop
```

### 2. Backend-Only Development
For API development without frontend:

```bash
# Option A: With Docker Compose (recommended)
make run

# Option B: Local development (requires local MySQL)
make boot
```

### 3. Testing Workflows

#### Quick Integration Tests
```bash
# Run integration tests with Testcontainers
make test
```
- Uses Testcontainers (no Docker Compose needed)
- Starts fresh MySQL container for each test run
- Fast feedback loop

#### Full Test Suite
```bash
# Run all tests (unit + integration)
make all-tests
```
- Comprehensive validation
- Includes code coverage
- Use before commits/PRs

#### Code Quality Checks
```bash
# Run static analysis
make lint

# Generate coverage report
make coverage
```

## Environment Requirements

### For `make run` and `make stop`
- Docker and Docker Compose
- No local MySQL needed

### For `make test` and `make all-tests`
- Docker (for Testcontainers)
- No Docker Compose needed
- No local MySQL needed

### For `make boot`
- Local MySQL 8.0+ running
- Database: `phoebe_db`
- User: `root` / Password: `root`
- Or configure via environment variables

## Configuration Details

### Docker Compose (`make run`)
- **MySQL**: Persistent data in Docker volume
- **Backend**: Hot reload with Spring Boot DevTools
- **Frontend**: Live reload (if configured)

### Testcontainers (`make test`)
- **MySQL**: Fresh container per test run
- **Isolation**: Each test gets clean database
- **Performance**: Optimized for CI/CD

### Local Development (`make boot`)
- **MySQL**: Your local installation
- **Flexibility**: Direct database access
- **Speed**: No container overhead

## Troubleshooting

### Build and Cache Issues
If the application behaves unexpectedly after code changes (especially in `build.gradle`),
try these commands in order:

1.  **`make rebuild`**: Rebuilds the backend without cache. This solves most dependency-related problems.
2.  **`make hard-rebuild`**: If `rebuild` doesn't help, this command performs a deeper clean before rebuilding.
3.  **`make reset`**: **CAUTION!** Use as a last resort. It removes containers and **all database data**,
    returning the environment to its initial state.

### Docker Issues
```bash
# If containers are stuck
make stop
docker system prune -f

# Restart
make run
```

### Test Issues
```bash
# Clean and retry
make clean
make test
```

### Local MySQL Issues
```bash
# Check MySQL status
brew services list | grep mysql

# Start MySQL (macOS with Homebrew)
brew services start mysql

# Create database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS phoebe_db;"
```

## Best Practices

1. **Use `make test` for quick feedback** during development
2. **Use `make all-tests` before commits** for full validation
3. **Use `make run` for manual testing** with frontend
4. **Use `make boot` only if you have local MySQL** and need direct access
5. **Always `make stop`** to clean up Docker resources

## Integration with IDEs

### IntelliJ IDEA
- Run configurations can use `make` commands
- Terminal integration: `Tools > Terminal`
- External tools: `Tools > External Tools`

### VS Code
- Tasks can be configured to run `make` commands
- Integrated terminal supports `make`
- Extensions available for Makefile syntax

## CI/CD Integration

The GitHub Actions workflow uses Testcontainers directly:
```yaml
- name: Run tests
  run: cd backend && ./gradlew clean build integrationTest jacocoTestReport --no-daemon
```

This is equivalent to running `make all-tests` locally.
