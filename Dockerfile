# Multi-stage Dockerfile for PET HUB
# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
# Download dependencies in a separate layer for faster caching
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S pethub && adduser -S pethub -G pethub
USER pethub

# Copy executable jar from builder
COPY --from=builder /app/target/pet-hub-1.0.0.jar app.jar

# Expose server port
EXPOSE 8080

# Run Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
