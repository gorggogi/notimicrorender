# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set the working directory for the build stage
WORKDIR /app

# Copy the Maven project file and source code
COPY notificationMicroservice/pom.xml .
COPY notificationMicroservice/src ./src

# Build the project
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight image
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8081

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
