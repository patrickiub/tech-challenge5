# syntax=docker/dockerfile:1

# Build context e a raiz do repo (multi-modulo Maven).

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Camada de cache de dependencias: so muda se os pom.xml mudarem.
COPY pom.xml .
COPY vaga-zero-api/pom.xml vaga-zero-api/pom.xml
COPY notificacao-service/pom.xml notificacao-service/pom.xml
RUN mvn -B -pl notificacao-service -am dependency:go-offline

# Codigo-fonte: muda a cada alteracao, nao invalida a camada acima.
COPY notificacao-service/src notificacao-service/src
RUN mvn -B -pl notificacao-service -am -DskipTests package

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/notificacao-service/target/notificacao-service-*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
