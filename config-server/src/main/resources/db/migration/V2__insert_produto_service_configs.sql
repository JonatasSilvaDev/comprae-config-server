-- Inserir/Atualizar configurações completas para o produto-service
-- Migration V2: Configurações centralizadas do microserviço de produtos
-- Usando UPSERT para evitar conflitos de chave duplicada

-- Configurações de Segurança e JWT
INSERT INTO configuracoes (chave, valor, namespace, ambiente, descricao) VALUES
('security.jwt.secret', 'meuSuperSecreto123!@#SecureKey2024CompraePlataform', 'produto-service', 'docker', 'Chave secreta para geração e validação de tokens JWT'),
('security.jwt.expiration', '86400', 'produto-service', 'docker', 'Tempo de expiração do token JWT em segundos (24h)'),
('security.jwt.issuer', 'comprae-platform', 'produto-service', 'docker', 'Emissor dos tokens JWT'),

-- Configurações de CORS
('allowed.origins', 'http://localhost:3000,http://localhost:8081,https://comprae.com.br', 'produto-service', 'docker', 'Origens permitidas para requisições CORS'),
('allowed.methods', 'GET,POST,PUT,DELETE,OPTIONS', 'produto-service', 'docker', 'Métodos HTTP permitidos'),
('allowed.headers', 'Content-Type,Authorization,X-Requested-With', 'produto-service', 'docker', 'Headers permitidos nas requisições'),

-- Configurações de Cache Multi-Níveis
('cache.levels', '2', 'produto-service', 'docker', 'Número de níveis de cache (L1: Caffeine, L2: Redis)'),
('cache.l1.maxSize', '1000', 'produto-service', 'docker', 'Tamanho máximo do cache L1 (Caffeine)'),
('cache.l1.expireAfter', '300', 'produto-service', 'docker', 'Tempo de expiração do cache L1 em segundos'),
('cache.l2.ttl', '3600', 'produto-service', 'docker', 'TTL do cache L2 (Redis) em segundos'),
('cache.enabled', 'true', 'produto-service', 'docker', 'Habilitar sistema de cache'),

-- Configurações de Banco de Dados
('database.url.prod', 'jdbc:postgresql://postgres-produto:5432/comprae_produtos', 'produto-service', 'docker', 'URL do banco de dados PostgreSQL'),
('database.username', 'admin', 'produto-service', 'docker', 'Usuário do banco de dados'),
('database.password', 'admin123', 'produto-service', 'docker', 'Senha do banco de dados'),
('database.pool.maxActive', '20', 'produto-service', 'docker', 'Número máximo de conexões ativas no pool'),
('database.pool.initialSize', '5', 'produto-service', 'docker', 'Tamanho inicial do pool de conexões'),

-- Configurações de Logging
('logging.level.dev', 'INFO', 'produto-service', 'docker', 'Nível de log para ambiente Docker'),
('logging.level.root', 'WARN', 'produto-service', 'docker', 'Nível de log raiz'),
('logging.level.br.com.comprae', 'DEBUG', 'produto-service', 'docker', 'Nível de log para pacotes da aplicação'),
('logging.pattern.console', '%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n', 'produto-service', 'docker', 'Padrão de log para console'),

-- Configurações do Redis
('redis.host', 'redis', 'produto-service', 'docker', 'Host do servidor Redis'),
('redis.port', '6379', 'produto-service', 'docker', 'Porta do servidor Redis'),
('redis.password', '', 'produto-service', 'docker', 'Senha do Redis (vazio para desenvolvimento)'),
('redis.timeout', '5000', 'produto-service', 'docker', 'Timeout de conexão com Redis em ms'),
('redis.database', '0', 'produto-service', 'docker', 'Número do database Redis'),

-- Configurações do Kafka
('kafka.bootstrap.servers', 'kafka:9092', 'produto-service', 'docker', 'Servidores Kafka'),
('kafka.consumer.groupId', 'produto-service-group', 'produto-service', 'docker', 'ID do grupo do consumidor Kafka'),
('kafka.producer.retries', '3', 'produto-service', 'docker', 'Número de tentativas do producer'),
('kafka.topic.produto.events', 'produto-events', 'produto-service', 'docker', 'Tópico para eventos de produtos'),

-- Configurações de Performance
('server.tomcat.maxThreads', '200', 'produto-service', 'docker', 'Número máximo de threads do Tomcat'),
('server.tomcat.minSpareThreads', '10', 'produto-service', 'docker', 'Número mínimo de threads do Tomcat'),
('server.compression.enabled', 'true', 'produto-service', 'docker', 'Habilitar compressão HTTP'),
('server.compression.minResponseSize', '1024', 'produto-service', 'docker', 'Tamanho mínimo para compressão'),

-- Configurações de Monitoramento
('management.metrics.enabled', 'true', 'produto-service', 'docker', 'Habilitar métricas do Actuator'),
('management.tracing.sampling.probability', '1.0', 'produto-service', 'docker', 'Probabilidade de sampling para tracing'),
('management.zipkin.tracing.endpoint', 'http://zipkin:9411/api/v2/spans', 'produto-service', 'docker', 'Endpoint do Zipkin para tracing'),

-- Configurações de Validação
('validation.produto.nome.maxLength', '100', 'produto-service', 'docker', 'Tamanho máximo do nome do produto'),
('validation.produto.descricao.maxLength', '1000', 'produto-service', 'docker', 'Tamanho máximo da descrição do produto'),
('validation.produto.preco.min', '0.01', 'produto-service', 'docker', 'Preço mínimo para um produto'),
('validation.produto.preco.max', '999999.99', 'produto-service', 'docker', 'Preço máximo para um produto'),

-- Configurações de Negócio
('business.produto.estoque.alertLevel', '10', 'produto-service', 'docker', 'Nível de alerta de estoque baixo'),
('business.produto.desconto.maxPercent', '70', 'produto-service', 'docker', 'Percentual máximo de desconto'),
('business.categoria.maxDepth', '5', 'produto-service', 'docker', 'Profundidade máxima da hierarquia de categorias'),

-- Configurações de Config Server Client
('config.client.server-url', 'http://comprae-config-server:8080', 'produto-service', 'docker', 'URL do servidor de configuração'),
('config.client.environment', 'docker', 'produto-service', 'docker', 'Ambiente para busca de configurações'),
('config.client.namespace', 'produto-service', 'produto-service', 'docker', 'Namespace para busca de configurações'),
('config.client.retry.maxAttempts', '3', 'produto-service', 'docker', 'Número máximo de tentativas de conexão'),
('config.client.timeout', '10000', 'produto-service', 'docker', 'Timeout para busca de configurações em ms')
ON CONFLICT (chave, namespace, ambiente) 
DO UPDATE SET 
    valor = EXCLUDED.valor,
    descricao = EXCLUDED.descricao,
    atualizado_em = CURRENT_TIMESTAMP;