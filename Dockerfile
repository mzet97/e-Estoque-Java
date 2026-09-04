# Build stage
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /workspace

# Cache de dependências: primeiro o pom
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -q dependency:go-offline || true

# Código e build
COPY src src
RUN ./mvnw -q clean package -DskipTests

# Runtime stage — JVM mínima, usuário não-root
FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app
RUN addgroup -S eestoque && adduser -S eestoque -G eestoque
USER eestoque
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=5s --start-period=40s \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q UP
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
