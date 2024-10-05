# Build stage
FROM maven:3.9.6-eclipse-temurin-22 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# Run stage
FROM eclipse-temurin:22-jre-jammy
WORKDIR /app


LABEL maintainer="nginepproject@gmail.com"
LABEL company="Nginep Project"

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

HEALTHCHECK --interval=30s --timeout=3s \
    CMD curl -f http://localhost:8080/actuator/health // exit 1