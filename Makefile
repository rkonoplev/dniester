# Makefile for Phoebe CMS project management

# Start the full project (Docker Compose: MySQL + backend + frontend)
run:
	docker-compose up

# Stop the project
stop:
	docker-compose down

# Run integration tests locally (Testcontainers)
test:
	cd backend && ./gradlew clean integrationTest

# Run all tests (unit + integration)
all-tests:
	cd backend && ./gradlew clean build

# Start backend locally without Docker (requires local MySQL)
boot:
	cd backend && ./gradlew bootRun

# Clean build artifacts
clean:
	cd backend && ./gradlew clean

# Run static analysis (Checkstyle + PMD)
lint:
	cd backend && ./gradlew checkstyleMain checkstyleTest pmdMain pmdTest

# Generate test coverage report
coverage:
	cd backend && ./gradlew jacocoTestReport

# Help command
help:
	@echo "Available commands:"
	@echo "  run        - Start full project (Docker Compose)"
	@echo "  stop       - Stop project"
	@echo "  test       - Run integration tests (Testcontainers)"
	@echo "  all-tests  - Run all tests (unit + integration)"
	@echo "  boot       - Start backend locally (requires local MySQL)"
	@echo "  clean      - Clean build artifacts"
	@echo "  lint       - Run static analysis"
	@echo "  coverage   - Generate test coverage report"
	@echo "  help       - Show this help"

.PHONY: run stop test all-tests boot clean lint coverage help