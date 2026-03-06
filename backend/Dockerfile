# ============================================================
# MEDI.WAY Backend Dockerfile
# Multi-stage build for optimal image size
# ============================================================

# Stage 1: Build Stage
FROM eclipse-temurin:17-jdk-alpine AS builder

LABEL maintainer="MEDI.WAY Team"
LABEL description="MEDI.WAY Healthcare Backend API"

WORKDIR /app

# Copy Maven wrapper and configuration
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Copy TestNG configuration (for testing in container)
COPY testng.xml .

# Build the application (skip tests for faster build - tests run separately in CI)
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime Stage
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="MEDI.WAY Team"
LABEL description="MEDI.WAY Healthcare Backend API - Runtime"

WORKDIR /app

# Create non-root user for security
RUN addgroup -S mediway && adduser -S mediway -G mediway

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Set ownership to non-root user
RUN chown -R mediway:mediway /app

# Switch to non-root user
USER mediway

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Entry point with JVM options
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
