# ==============================================================================
# Dockerfile - VitalBite Documental Microservice (Production)
# Optimizado para DigitalOcean App Platform
# ==============================================================================

# ========== STAGE 1: BUILD ==========
# Usar maven con JDK 21 para compilar
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar pom.xml y resolver dependencias (aprovecha capas)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar el proyecto
RUN mvn clean package -DskipTests -B

# ========== STAGE 2: RUNTIME ==========
# Usar JDK 21 slim (más pequeño que completo)
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Metadata
LABEL maintainer="VitalBite Team"
LABEL description="VitalBite Documental Microservice - Production"

# Instalar curl para healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para seguridad
RUN useradd -m -u 1001 appuser

# Copiar JAR compilado desde la etapa anterior
COPY --from=builder /app/target/documental-0.0.1-SNAPSHOT.jar app.jar
COPY --from=builder /app/target/classes/application.properties .

# Cambiar permisos
RUN chown -R appuser:appuser /app && \
    chmod +x /app/app.jar

# Cambiar al usuario no-root
USER appuser

# Puerto expuesto (el que usa Spring Boot + context-path)
EXPOSE 8082

# Variables de entorno con valores por defecto (se sobrescriben en DigitalOcean)
ENV SPRING_PROFILES_ACTIVE=production \
    LOG_LEVEL=INFO \
    JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8082/api/v1/actuator/health || exit 1

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
