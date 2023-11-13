FROM maven:3.8.3-openjdk-8 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

FROM amazoncorretto:8-alpine3.17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY opentelemetry-javaagent.jar ./opentelemetry-javaagent.jar
COPY opentelemetry-javaagent.jar ./opentelemetry-javaagent.jar
EXPOSE 8080
#ENTRYPOINT ["java", "-Dserver.port=8080", "-javaagent:/opentelemetry-javaagent.jar", "-jar", "app.jar"]
ENTRYPOINT ["java", "-Dserver.port=8080", "-javaagent:/opentelemetry-javaagent-all.jar -Dotel.trace.exporter=jaeger -Dotel.exporter.jaeger.endpoint=tempo.tracing.svc:14250  -Dotel.resource.attributes=service.name=spring-boot-instrumentation -Dotel.javaagent.debug=false -Dotel.metrics.exporter=none", "-jar", "app.jar"]

