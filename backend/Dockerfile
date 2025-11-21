## Multi-stage build for Spring Boot backend
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy only build descriptors first for better layer caching
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw -q -DskipTests dependency:go-offline || true

# Copy source and build
COPY src src
RUN ./mvnw -q -DskipTests package

## Use a multi-arch base image (Debian slim) to avoid alpine arm64 mismatch
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
RUN useradd -ms /bin/sh app

ARG JAR_FILE="/workspace/target/*.jar"
COPY --from=build ${JAR_FILE} app.jar
RUN chown app:app app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
USER app
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]