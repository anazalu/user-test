# FROM openjdk:17-jdk
# WORKDIR /app
# COPY target/user-api-0.0.1-SNAPSHOT.jar api.jar
# EXPOSE 8080
# CMD ["java", "-jar", "api.jar"]

FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk
WORKDIR /app

COPY --from=builder /app/target/*.jar api.jar

EXPOSE 8080
CMD ["java", "-jar", "api.jar"]