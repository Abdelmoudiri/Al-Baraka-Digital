

FROM maven:3.9.3-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

from eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --form=builder app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]