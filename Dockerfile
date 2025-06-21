# Stage 1: Build the Spring Boot application
FROM openjdk:17-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper files and pom.xml to leverage Docker caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (this layer changes less frequently)
# Use -B for non-interactive mode
RUN ./mvnw dependency:go-offline -B

# Copy source code and build the application
COPY src ./src
# Build the JAR, skipping tests for faster builds
RUN ./mvnw clean install -DskipTests

# Stage 2: Create the final, lightweight runtime image
FROM openjdk:17-jdk-alpine
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port (Spring Boot defaults to 8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]