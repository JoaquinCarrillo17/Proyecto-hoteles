# Etapa 1: Compilación
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY ./proyecto-hoteles/pom.xml ./
COPY ./proyecto-hoteles/src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY --from=build /app/target/hoteles-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
