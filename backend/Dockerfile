# syntax=docker/dockerfile:1.4

# ============================================
# Stage 1: Build
# ============================================
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /workspace

# Copy Gradle wrapper and build files
COPY backend/gradle/ gradle/
COPY backend/gradlew backend/gradlew.bat ./
COPY backend/settings.gradle.kts ./
COPY backend/build.gradle.kts ./

# Copy all module build files (if any)
COPY backend/api/build.gradle.kts api/
COPY backend/core/build.gradle.kts core/
COPY backend/infra/build.gradle.kts infra/
COPY backend/ai-core/build.gradle.kts ai-core/
COPY backend/ai-ops/build.gradle.kts ai-ops/
COPY backend/ai-insights/build.gradle.kts ai-insights/
COPY backend/ai-gateway/build.gradle.kts ai-gateway/
COPY backend/signer/build.gradle.kts signer/
COPY backend/soap/build.gradle.kts soap/
COPY backend/ubl/build.gradle.kts ubl/

# Cache dependencies
RUN chmod +x ./gradlew && \
    ./gradlew dependencies --no-daemon || true

# Copy source code
COPY backend/ .

# Build the application
RUN ./gradlew :api:bootJar --no-daemon --stacktrace

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:21-jre-jammy

# Create non-root user
RUN groupadd -r appuser && useradd -r -g appuser -u 1001 appuser

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy jar from builder
COPY --from=builder /workspace/api/build/libs/*.jar /app/app.jar

# Set ownership
RUN chown -R appuser:appuser /app

# JVM Options
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Switch to non-root user
USER appuser

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]