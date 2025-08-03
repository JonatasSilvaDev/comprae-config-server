# 🛠️ Compraê Config Server

Servidor de configuração centralizada para o projeto **Compraê**, fornecendo gerenciamento dinâmico de configurações para todos os microserviços da plataforma.

---

## 📌 Objetivo

Centralizar e gerenciar configurações de todos os microserviços do projeto Compraê, oferecendo:

- **Gerenciamento centralizado** de configurações por namespace e ambiente
- **Versionamento e histórico** de todas as alterações
- **Cache distribuído** para alta performance
- **Notificações em tempo real** via Kafka
- **Interface REST** para integração com serviços
- **Segurança** com autenticação e autorização
- **Monitoramento** com métricas e health checks

---

## 🏗️ Arquitetura

```
┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────────┐
│   Microserviços     │    │   Config Server     │    │   Infraestrutura    │
│                     │    │                     │    │                     │
│ • comprae-api       │◄──►│ • REST API          │◄──►│ • PostgreSQL        │
│ • comprae-frontend  │    │ • Cache (Redis)     │    │ • Redis             │
│ • comprae-payment   │    │ • Kafka Events      │    │ • Kafka             │
│ • comprae-auth      │    │ • Security          │    │ • Prometheus        │
└─────────────────────┘    └─────────────────────┘    └─────────────────────┘
```

---

## 🗂️ Estrutura do Projeto

```
config-server/
├── src/
│   ├── main/
│   │   ├── java/com/configsystem/server/
│   │   │   ├── ConfigServerApplication.java
│   │   │   ├── config/              # Configurações do Spring
│   │   │   ├── controlador/         # Controllers REST
│   │   │   ├── dto/                 # DTOs para API
│   │   │   ├── entidade/            # Entidades JPA
│   │   │   ├── evento/              # Eventos Kafka
│   │   │   ├── excecao/             # Exceções customizadas
│   │   │   ├── repositorio/         # Repositórios JPA
│   │   │   └── servico/             # Serviços de negócio
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-docker.properties
│   │       └── db/migration/        # Scripts Flyway
│   └── test/                        # Testes unitários e integração
├── Dockerfile                       # Container Docker
├── pom.xml                          # Dependências Maven
└── README.md
```

---

## ⚙️ Tecnologias Utilizadas

### Core
- **Java 17** - Linguagem de programação
- **Spring Boot 3.5.4** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Autenticação e autorização
- **Spring Cache** - Sistema de cache
- **Spring Kafka** - Mensageria assíncrona

### Banco de Dados
- **PostgreSQL** - Banco de dados principal
- **Flyway** - Migração e versionamento do banco
- **Redis** - Cache distribuído

### Monitoramento
- **Spring Actuator** - Health checks e métricas
- **Micrometer** - Coleta de métricas
- **Prometheus** - Monitoramento (opcional)
- **Grafana** - Visualização (opcional)

### Documentação
- **SpringDoc OpenAPI** - Documentação automática da API

### Testes
- **JUnit 5** - Framework de testes
- **Testcontainers** - Testes de integração
- **Spring Security Test** - Testes de segurança

---

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- Docker e Docker Compose (recomendado)

### Execução com Docker Compose (Recomendado)

1. **Clone o repositório:**
```bash
git clone <repository-url>
cd comprae-config-server
```

2. **Execute o ambiente completo:**
```bash
# Ambiente básico (PostgreSQL, Redis, Kafka + Config Server)
docker-compose up -d

# Com monitoramento (inclui Prometheus e Grafana)
docker-compose --profile monitoring up -d
```

3. **Verifique se está funcionando:**
```bash
curl http://localhost:8080/actuator/health
```

### Execução Local (Desenvolvimento)

1. **Configure o banco de dados local:**
```bash
# PostgreSQL
docker run -d --name postgres \
  -e POSTGRES_DB=configdb \
  -e POSTGRES_USER=configuser \
  -e POSTGRES_PASSWORD=configpass \
  -p 5432:5432 postgres:15-alpine

# Redis
docker run -d --name redis -p 6379:6379 redis:7-alpine

# Kafka (ou use docker-compose apenas para infraestrutura)
docker-compose up -d postgres redis kafka zookeeper
```

2. **Execute a aplicação:**
```bash
cd config-server
./mvnw spring-boot:run
```

---

## � API Endpoints

### Configurações

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/configuracao/{chave}` | Buscar configuração específica |
| GET | `/api/configuracao/todas` | Buscar todas as configurações |
| POST | `/api/configuracao` | Criar/atualizar configuração |
| PUT | `/api/configuracao/{id}` | Atualizar configuração existente |
| DELETE | `/api/configuracao/{id}` | Remover configuração |

### Histórico

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/configuracao/historico/{chave}` | Buscar histórico de uma configuração |
| GET | `/api/configuracao/historico` | Buscar histórico por filtros |

### Gerenciamento

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/configuracao/namespaces` | Listar namespaces disponíveis |
| GET | `/api/configuracao/ambientes` | Listar ambientes disponíveis |
| POST | `/api/configuracao/sincronizar` | Sincronizar configurações |

### Monitoramento

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/actuator/health` | Status da aplicação |
| GET | `/actuator/metrics` | Métricas da aplicação |
| GET | `/actuator/prometheus` | Métricas no formato Prometheus |

### Documentação

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/swagger-ui.html` | Interface Swagger |
| GET | `/api-docs` | Especificação OpenAPI |

---

## 🔧 Configuração

### Variáveis de Ambiente

```bash
# Banco de dados
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/configdb
SPRING_DATASOURCE_USERNAME=configuser
SPRING_DATASOURCE_PASSWORD=configpass

# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Segurança
USERNAME=admin
PASSWORD=admin123

# Perfil ativo
SPRING_PROFILES_ACTIVE=desenvolvimento
```

### Exemplos de Uso da API

**Buscar uma configuração:**
```bash
curl "http://localhost:8080/api/configuracao/database.url?namespace=comprae-api&ambiente=desenvolvimento"
```

**Criar/atualizar configuração:**
```bash
curl -X POST http://localhost:8080/api/configuracao \
  -H "Content-Type: application/json" \
  -d '{
    "chave": "nova.configuracao",
    "valor": "valor_exemplo",
    "namespace": "comprae-api",
    "ambiente": "desenvolvimento",
    "descricao": "Descrição da configuração"
  }'
```

**Buscar histórico:**
```bash
curl "http://localhost:8080/api/configuracao/historico/database.url?namespace=comprae-api&ambiente=desenvolvimento"
```

---

## 🧪 Testes

```bash
# Executar todos os testes
./mvnw test

# Executar apenas testes unitários
./mvnw test -Dtest="*Test"

# Executar apenas testes de integração
./mvnw test -Dtest="*IT"

# Executar com cobertura
./mvnw test jacoco:report
```

---

## 📊 Monitoramento

### Métricas Disponíveis
- Número de configurações por namespace/ambiente
- Tempo de resposta das operações
- Cache hit/miss ratio
- Número de eventos Kafka enviados
- Status das conexões com banco e Redis

### Dashboards Grafana
Quando executado com o perfil de monitoramento, dashboards pré-configurados estão disponíveis em:
- **Grafana:** http://localhost:3000 (admin/admin123)
- **Prometheus:** http://localhost:9090

---

## � Segurança

- Autenticação básica configurável via variáveis de ambiente
- CORS configurado para desenvolvimento
- Validação de entrada em todos os endpoints
- Logs de auditoria para todas as operações

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

---

## 📝 Licença

Este projeto está sob a licença [MIT](LICENSE).

---

## 📞 Suporte

Para dúvidas ou problemas:
- Abra uma issue no GitHub
- Entre em contato com a equipe de desenvolvimento
    name: config-server

  cloud:
    config:
      server:
        git:
          uri: https://github.com/sua-organizacao/comprae-config-repo
          default-label: main
          search-paths: config-repo
```

🚀 Como executar localmente  
1 - Clone este repositório:

```
git clone https://github.com/sua-organizacao/comprae-config-server.git
cd comprae-config-server
```
2 - Execute com Maven:
```
./mvnw spring-boot:run
```
3 - Verifique no navegador ou via cURL:
```
curl http://localhost:8888/produtos/default
```
📂 Repositório de configurações  
Todos os arquivos .yml utilizados pelos serviços ficam no repositório:

🔗 comprae-config-repo

✅ Serviços consumidores  
Os seguintes microserviços consomem as configurações centralizadas:

| Serviço                 | Nome da aplicação |
| ----------------------- | ----------------- |
| API de Produtos         | `produtos`        |
| API de Pedidos          | `pedidos`         |
| Serviço de Autenticação | `auth-service`    |
| Frontend (se aplicável) | `frontend`        |


📄 Licença  
Projeto desenvolvido para fins educacionais e de demonstração interna.
Todos os direitos reservados à equipe responsável pelo Compraê.



