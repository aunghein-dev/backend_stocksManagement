# --- Step 1: Build Stage ---
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy everything
COPY . .

# Build with Maven wrapper (make sure it's executable!)
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# --- Step 2: Runtime Stage ---
FROM openjdk:17-jdk-alpine
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run
ENTRYPOINT ["java", "-jar", "app.jar"]
