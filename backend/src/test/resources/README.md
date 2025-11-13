# Test Application Configuration

This directory can be used to hold configuration files that are loaded exclusively when running tests.

Any file placed here (e.g., `application.yml` or `application-integration-test.yml`) will override the file with the same name from the `src/main/resources` directory during a test run. This allows for the creation of a specific, controlled, and isolated test environment.

Currently, the main test configuration file (`application-integration-test.yml`) is located in `src/main/resources`. However, you can place a file here if you need to override specific properties for a local test run without affecting the main configuration.