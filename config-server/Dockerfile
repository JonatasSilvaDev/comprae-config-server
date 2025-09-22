# Dockerfile simplificado - usando JAR pré-compilado
FROM eclipse-temurin:17-jre-alpine

# Criar usuário não-root para segurança
RUN addgroup -g 1001 -S configserver && \
    adduser -u 1001 -S configserver -G configserver

WORKDIR /app

# Copiar o JAR pré-compilado
COPY target/config-server-*.jar app.jar

# Alterar propriedade do arquivo para o usuário criado
RUN chown configserver:configserver app.jar

# Configurar usuário não-root
USER configserver

# Expor porta da aplicação
EXPOSE 8080
EXPOSE 5005

# Configurações de JVM otimizadas (padrão)
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando de inicialização (sem debug fixo)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
