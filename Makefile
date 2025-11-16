# =======================================================
# Makefile for Phoebe CMS project management
# =======================================================
# This Makefile provides common shortcuts for development,
# testing, building, and cleaning the project environment.
# =======================================================

# Start the full project (MySQL + backend + frontend)
# Always builds (using cache) before running
run:
	docker compose up --build

# Stop all running containers (keeps database volume)
stop:
	docker compose down

# Rebuild the backend image without using cache (clean build) and start
rebuild:
	docker compose build --no-cache phoebe-app
	docker compose up

# Hard rebuild: stop everything, remove containers, rebuild backend without cache, and start
# Use this if Docker caching or old images cause issues
hard-rebuild:
	docker compose down
	docker compose build --no-cache phoebe-app
	docker compose up

# Reset the entire environment (remove containers + volumes)
# Warning: this deletes your MySQL data volume
reset:
	docker compose down -v

# Run integration tests (Testcontainers)
test:
	cd backend && ./gradlew clean integrationTest

# Run all tests (unit + integration)
all-tests:
	cd backend && ./gradlew clean build

# Start backend locally (without Docker, requires local MySQL)
boot:
	cd backend && ./gradlew bootRun

# Clean build artifacts
clean:
	cd backend && ./gradlew clean

# Run static analysis tools (Checkstyle + PMD)
lint:
	cd backend && ./gradlew checkstyleMain checkstyleTest pmdMain pmdTest

# Generate test coverage report
coverage:
	cd backend && ./gradlew jacocoTestReport

# Show help with available commands
help:
	@echo "Available commands:"
	@echo "  run           - Start full project (Docker Compose, builds with cache)"
	@echo "  stop          - Stop project (keeps data)"
	@echo "  rebuild       - Rebuild backend without cache and start"
	@echo "  hard-rebuild  - Full clean rebuild (down + no-cache + up)"
	@echo "  reset         - Stop and remove containers, networks, volumes (delete data)"
	@echo "  test          - Run integration tests (Testcontainers)"
	@echo "  all-tests     - Run all tests (unit + integration)"
	@echo "  boot          - Start backend locally (requires local MySQL)"
	@echo "  clean         - Clean Gradle build artifacts"
	@echo "  lint          - Run static analysis (Checkstyle + PMD)"
	@echo "  coverage      - Generate test coverage report"
	@echo "  help          - Show this help message"

.PHONY: run stop rebuild hard-rebuild reset test all-tests boot clean lint coverage help