# =========================================================
# Dockerfile
# Production build image.
# Packages app into a JAR and runs in minimal JRE-only container.
# =========================================================

FROM eclipse-temurin:17-jre as runtime
WORKDIR /app

# Copy built JAR (from gradlew bootJar)
COPY backend/build/libs/*.jar app.jar

# Use externalized configs: Render Secret Files or ENV vars
ENTRYPOINT ["java", "-jar", "app.jar"]