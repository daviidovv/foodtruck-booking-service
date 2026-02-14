# ===========================================
# Multi-Stage Dockerfile für Foodtruck Booking
# Baut Frontend + Backend in einem Image
# ===========================================

# Stage 1: Frontend Build
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend

# Dependencies zuerst (für besseres Caching)
COPY frontend/package*.json ./
RUN npm ci --silent

# Frontend bauen
COPY frontend/ ./
RUN npm run build

# Stage 2: Backend Build
FROM maven:3.9-eclipse-temurin-21-alpine AS backend-build
WORKDIR /app

# Dependencies zuerst (für besseres Caching)
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Source kopieren
COPY src ./src

# Frontend-Build ins Backend kopieren (static resources)
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static

# Backend bauen
RUN mvn clean package -DskipTests -B

# Stage 3: Runtime Image
FROM eclipse-temurin:21-jre-alpine AS runtime

# Security: Non-root user
RUN addgroup -g 1001 appgroup && \
    adduser -u 1001 -G appgroup -D appuser

WORKDIR /app

# JAR kopieren
COPY --from=backend-build /app/target/*.jar app.jar

# Ownership setzen
RUN chown -R appuser:appgroup /app

USER appuser

# Environment
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080

# Health Check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
