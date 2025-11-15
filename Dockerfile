# =========================================================
# Dockerfile
# Production build image.
# Uses multi-stage build for reproducibility and minimal size.
# =========================================================

# --- Stage 1: Build ---
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY backend/gradlew ./
COPY backend/gradle ./gradle/
COPY backend/build.gradle backend/settings.gradle ./
COPY backend/src ./src/

# Build the JAR (produces backend/build/libs/*-boot.jar)
RUN chmod +x ./gradlew && ./gradlew bootJar --no-daemon

# --- Stage 2: Runtime ---
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# Copy the fat JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]