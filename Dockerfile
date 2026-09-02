# ============================================================
# ETAPA 1: Compilación
# ============================================================

FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copiamos primero el pom.xml para aprovechar la caché de Docker.
COPY pom.xml .

# Descarga las dependencias necesarias.
RUN mvn dependency:go-offline

# Copiamos el código fuente.
COPY src ./src

# Compilamos y generamos el JAR.
RUN mvn clean package -DskipTests


# ============================================================
# ETAPA 2: Ejecución
# ============================================================

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiamos únicamente el JAR generado en la etapa anterior.
COPY --from=build /app/target/*.jar app.jar

# Puerto utilizado por Spring Boot.
EXPOSE 8080

# Arranca la aplicación.
ENTRYPOINT ["java", "-jar", "app.jar"]