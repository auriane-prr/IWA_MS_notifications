# --- Build stage for Spring Boot application ---
FROM gradle:8-jdk17 AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle build files
COPY build.gradle.kts settings.gradle.kts ./

# Pre-fetch dependencies to leverage Docker layer caching
RUN gradle build --no-daemon -x test

# Copy the application source code
COPY . .

# Build the application, skipping tests to speed up the process
RUN gradle build --no-daemon -x test

# --- Runtime stage ---
FROM openjdk:21-jdk-slim

# Set the working directory for the application
WORKDIR /app

# Copy the built application jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8085

# Start the application
CMD ["java", "-jar", "app.jar"]