> [Back to Documentation Contents](./README.md)

# Testing Strategy Guide

> **⚠️ WARNING: THIS DOCUMENT IS OUTDATED**
>
> This document describes the **old, hybrid** testing strategy that was used before the full migration to Testcontainers. It is preserved for historical context.
>
> **For current information, please use the following documents:**
> - **[Testing with Makefile](./TESTING_WITH_MAKEFILE.md)** (for practical test execution)
> - **[ADR: Unifying the Testing Strategy with Testcontainers](./TESTCONTAINERS_EVOLUTION.md)** (to understand the architectural decisions)

---

(The original, untouched content of the file follows)

## Testing Architecture Overview

### Test Types and Environments

| Test Type | Environment | Database | Base Class | Profile |
|-----------|-------------|----------|------------|---------|
| **Unit Tests** | Any | Mocks only | N/A | `test` |
| **Integration Tests** | All (Local & CI) | Testcontainers MySQL | `BaseIntegrationTest` | `integration-test` |

... and so on.
