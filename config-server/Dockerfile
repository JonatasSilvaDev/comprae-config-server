# Dockerfile multi-stage para build e execução otimizada
# Stage 1: Build da aplicação
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copiar apenas os arquivos necessários para o build
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Baixar dependências (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copiar código fonte e fazer build
COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Criar usuário não-root para segurança
RUN addgroup -g 1001 -S configserver && \
    adduser -u 1001 -S configserver -G configserver

WORKDIR /app

# Copiar apenas o JAR da aplicação do stage anterior
COPY --from=builder /app/target/config-server-*.jar app.jar

# Alterar propriedade do arquivo para o usuário criado
RUN chown configserver:configserver app.jar

# Configurar usuário não-root
USER configserver

# Expor porta da aplicação
EXPOSE 8080

# Configurações de JVM otimizadas
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
