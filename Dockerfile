# Stage 1 — Build the jar using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Stage 2 — Run the jar using Java 17
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/labor-optimizer-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]