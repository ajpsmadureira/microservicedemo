# Build stage
FROM maven:3.9.6-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app

# Create directory for file uploads
RUN mkdir -p /app/uploads && \
    chmod 777 /app/uploads

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV APP_FILE_STORAGE_LOCATION=/app/uploads

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"] 