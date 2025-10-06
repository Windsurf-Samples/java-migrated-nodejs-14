# Multi-stage build for Reaction Commerce Java API

# Build stage
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (for better layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Copy the built jar
COPY --from=build /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app
USER appuser

# Expose port
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:3000/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
