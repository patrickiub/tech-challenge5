# syntax=docker/dockerfile:1

# Build context e a raiz do repo (multi-modulo Maven).

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Camada de cache de dependencias: so muda se os pom.xml mudarem.
COPY pom.xml .
COPY vaga-zero-api/pom.xml vaga-zero-api/pom.xml
COPY notificacao-service/pom.xml notificacao-service/pom.xml
RUN mvn -B -pl vaga-zero-api -am dependency:go-offline

# Codigo-fonte: muda a cada alteracao, nao invalida a camada acima.
COPY vaga-zero-api/src vaga-zero-api/src
RUN mvn -B -pl vaga-zero-api -am -DskipTests package

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/vaga-zero-api/target/vaga-zero-api-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
