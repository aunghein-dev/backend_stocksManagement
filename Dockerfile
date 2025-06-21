# Java 17 image
FROM openjdk:17-jdk-alpine

# Set directory
WORKDIR /app

# Copy JAR
COPY target/*.jar app.jar

# Run backend
ENTRYPOINT ["java", "-jar", "app.jar"]
