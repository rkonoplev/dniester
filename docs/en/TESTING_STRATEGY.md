# Testing Strategy Guide

This document explains the comprehensive testing strategy used in Phoebe CMS, including the hybrid approach for integration testing.

---

## Testing Architecture Overview

### Test Types and Environments

| Test Type | Environment | Database | Base Class | Profile |
|-----------|-------------|----------|------------|---------|
| **Unit Tests** | Any | Mocks only | N/A | `test` |
| **Local Integration** | Local Dev | Testcontainers MySQL | `LocalIntegrationTest` | `integration-test` |
| **CI Integration** | GitHub Actions | Docker Compose MySQL | `AbstractIntegrationTest` | `ci-integration` |

---

## Integration Test Strategy

### Local Development
```java
// For local development - automatic Testcontainers
@ActiveProfiles("integration-test")
@Testcontainers
class NewsServiceTest extends LocalIntegrationTest {
    // MySQL container automatically started/stopped
    // No Docker Compose setup required
}
```

### CI Environment
```java
// For CI - uses external Docker Compose MySQL
@ActiveProfiles("ci-integration") 
class NewsServiceTest extends AbstractIntegrationTest {
    // Uses shared MySQL service from Docker Compose
    // Faster execution, no container startup overhead
}
```

---

## Environment Configuration

### Gradle Task Configuration
```bash
# Local development
./gradlew integrationTest
# Uses LocalIntegrationTest with Testcontainers

# CI environment  
./gradlew integrationTest -Pci
# Uses AbstractIntegrationTest with Docker Compose MySQL
```

### Profile Selection Logic
- **Default**: `integration-test` profile → Testcontainers
- **CI Parameter**: `-Pci` → `ci-integration` profile → Docker Compose
- **Automatic**: Tests extend appropriate base class based on environment

---

## Benefits of Hybrid Approach

### Local Development Benefits
- **Zero Setup**: No Docker Compose required
- **Isolation**: Each test run gets fresh database
- **Debugging**: Easy to debug with container logs
- **Flexibility**: Different MySQL versions per test if needed

### CI Environment Benefits  
- **Speed**: Reuses shared MySQL service
- **Resource Efficiency**: No container startup overhead
- **Reliability**: Consistent with production deployment
- **Simplicity**: Single MySQL service for all tests

---

## Migration Guide

### From Old Approach
```java
// OLD: Single AbstractIntegrationTest with complex logic
@Testcontainers
class MyTest extends AbstractIntegrationTest {
    // Complex conditional Testcontainers logic
}
```

### To New Approach
```java
// NEW: Choose appropriate base class
class MyTest extends LocalIntegrationTest {     // For local development
class MyTest extends AbstractIntegrationTest { // For CI environment
```

---

## Best Practices

1. **Use LocalIntegrationTest** for local development and debugging
2. **Use AbstractIntegrationTest** for CI-compatible tests
3. **Keep unit tests fast** with mocks only
4. **Test database migrations** in integration tests
5. **Verify production parity** with real MySQL in all integration tests

---

## Troubleshooting

### Common Issues
- **Testcontainers not starting**: Check Docker daemon is running locally
- **CI tests failing**: Ensure Docker Compose MySQL is healthy before tests
- **Profile conflicts**: Verify correct base class and profile combination

### Debug Commands
```bash
# Check CI MySQL status
docker compose ps phoebe-mysql

# Run specific test locally
./gradlew integrationTest --tests "NewsServiceTest"

# Run with CI profile locally
./gradlew integrationTest -Pci --tests "NewsServiceTest"
```