# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install wget for healthcheck
RUN apk add --no-cache wget

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create directories for volumes
RUN mkdir -p /app/uploads /app/secrets

# Environment variables (Defaults, can be overridden by docker-compose)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/bena_db
ENV SPRING_DATASOURCE_USERNAME=bena_user
ENV SPRING_DATASOURCE_PASSWORD=StrongPassword123

EXPOSE 8080

# JVM Optimizations for production
ENTRYPOINT ["java", \
    "-Xms256m", \
    "-Xmx768m", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-XX:+UseStringDeduplication", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
