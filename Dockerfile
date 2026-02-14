FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
