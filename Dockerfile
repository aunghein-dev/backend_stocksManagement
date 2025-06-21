# --- Step 1: Build Stage ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy all source files
COPY . .

# Package the application (skip tests to speed up)
RUN ./mvnw clean package -DskipTests

# --- Step 2: Runtime Stage ---
FROM openjdk:17-jdk-alpine
WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=builder /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
