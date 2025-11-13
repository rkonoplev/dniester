# ADR: Unifying the Testing Strategy with Testcontainers

This document is an Architectural Decision Record (ADR) that describes the historical context and reasoning behind the move to a unified testing strategy using Testcontainers.

> **Important:** This document explains *why* the current strategy was adopted. For practical instructions on running tests and development, please refer to the following guides:
> - **[Testing and Development with Makefile](./TESTING_WITH_MAKEFILE.md)** (for local workflows)
> - **[CI/CD Guide](./CI_CD_GUIDE.md)** (for understanding the CI process)

---

## Historical Context

Initially, the project used a hybrid testing approach, which involved different strategies for local development, CI, and unit tests. This led to environment inconsistencies, complicated setup, and reduced test reliability.

The goals of migrating to Testcontainers were:
1.  **Unify the Environment**: Use MySQL across all environments (local, CI, production).
2.  **Isolate Tests**: Ensure that each test run operates with a clean database.
3.  **Simplify Development**: Automate the database lifecycle for tests, removing the need for manual setup.

## Architecture Comparison

| Aspect | Previous Hybrid Approach | Current Unified Approach |
|---|---|---|
| **Production Parity** | Medium (H2/Docker Compose) | High (MySQL everywhere) |
| **Test Reliability** | Medium (environment differences) | High (identical environments) |
| **Setup Complexity** | High (multiple configurations) | Low (single approach via `Makefile` and `BaseIntegrationTest`) |
| **CI/CD Consistency** | Medium | High (Testcontainers is used both locally and in CI) |

---

## Adopted Decision: Unified Approach with Testcontainers

The decision was made to completely switch to Testcontainers for all integration tests, abandoning H2 and manual Docker Compose management for tests.

### Technical and Strategic Benefits

1.  **Production-First Approach**: Testing is always performed on the same database technology as in production.
2.  **Reliability and Consistency**: Tests behave identically on a developer's machine and in the CI pipeline.
3.  **Simplified Architecture**: A single base class `BaseIntegrationTest` and profile `integration-test` for all integration tests.
4.  **Developer Confidence**: Successful test runs provide greater confidence that the code will work in production.
5.  **Simplified CI**: The CI pipeline does not depend on external database services or complex setups.

---

## Conclusion

The project has fully transitioned to a unified testing strategy where **Testcontainers** is the sole tool for managing databases in integration tests. This decision has led to high consistency, reliability, and simplification of both local development and the continuous integration process.