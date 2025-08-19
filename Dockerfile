# ---------- build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache deps
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN mvn -q -DskipTests clean package

# ---------- run stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy Spring Boot fat jar
COPY --from=build /app/target/*.jar app.jar

# App listens on 8080 inside the container
EXPOSE 8080

# Allow optional JVM tuning via JAVA_OPTS (not required)
ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
