# ----- Build Stage -----
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copia el pom.xml y descarga las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

COPY backend ./backend

# Compila y empaqueta la aplicación en un JAR ejecutable.
RUN mvn clean package

# ----- Run Stage -----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia el archivo JAR compilado desde la etapa de "build".
COPY --from=build /app/target/*.jar app.jar

# Define el comando para ejecutar la aplicación.
ENTRYPOINT ["java", "-jar", "app.jar"]