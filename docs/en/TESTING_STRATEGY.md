# Testing Strategy Guide

This document explains the unified testing strategy used in Phoebe CMS with Testcontainers everywhere.

---

## Testing Architecture Overview

### Test Types and Environments

| Test Type | Environment | Database | Base Class | Profile |
|-----------|-------------|----------|------------|---------|
| **Unit Tests** | Any | Mocks only | N/A | `test` |
| **Integration Tests** | All (Local & CI) | Testcontainers MySQL | `BaseIntegrationTest` | `integration-test` |

---

## Unified Integration Test Strategy

### All Environments (Local & CI)
```java
// Unified approach - works identically everywhere
@ActiveProfiles("integration-test")
@Testcontainers
class NewsServiceTest extends BaseIntegrationTest {
    // MySQL container automatically started/stopped
    // Identical behavior in local development and CI
    // No manual Docker setup required anywhere
}
```

---

## Environment Configuration

### Gradle Task Configuration
```bash
# Works identically in all environments
./gradlew integrationTest
# Uses BaseIntegrationTest with Testcontainers everywhere
```

### Profile Selection Logic
- **Single Profile**: `integration-test` for all integration tests
- **Automatic**: All tests extend `BaseIntegrationTest`
- **Consistent**: Identical behavior across all platforms

---

## Benefits of Unified Approach

### Universal Benefits
- **Zero Setup**: No Docker Compose required anywhere
- **Consistency**: Identical test environments everywhere
- **Isolation**: Each test run gets fresh database
- **Simplicity**: Single approach for all environments
- **Reliability**: No environment-specific configuration issues

### Local Development Benefits
- **No Manual Setup**: Testcontainers handles everything
- **Easy Debugging**: Container logs available
- **Fast Iteration**: Quick test cycles

### CI Environment Benefits  
- **Simplified Pipeline**: No Docker Compose setup
- **Resource Efficiency**: Automatic container management
- **Reliability**: No external dependencies
- **Faster Execution**: No manual service coordination

---

## Migration Guide

### From Hybrid Approach
```java
// OLD: Multiple base classes for different environments
class MyTest extends LocalIntegrationTest {     // Local only
class MyTest extends AbstractIntegrationTest { // CI only
```

### To Unified Approach
```java
// NEW: Single base class for all environments
class MyTest extends BaseIntegrationTest {
    // Works identically everywhere
}
```

---

## Best Practices

1. **Use BaseIntegrationTest** for all integration tests
2. **Keep unit tests fast** with mocks only
3. **Test database migrations** in integration tests
4. **Verify production parity** with real MySQL in all tests
5. **Let Testcontainers manage lifecycle** - no manual container management

---

## Troubleshooting

### Common Issues
- **Testcontainers not starting**: Check Docker daemon is running
- **Tests hanging**: Kill hanging processes and containers (see Git Bash Commands guide)
- **Memory issues**: Ensure sufficient Docker memory allocation

### Debug Commands
```bash
# Run specific test
./gradlew integrationTest --tests "NewsServiceTest"

# Check Docker containers
docker ps

# Clean up hanging containers
docker stop $(docker ps -q --filter "label=org.testcontainers")

# Check test logs
./gradlew integrationTest --info
```