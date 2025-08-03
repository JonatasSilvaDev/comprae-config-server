# 🛠️ Compraê Config Server

Repositório responsável pelo **servidor de configuração centralizada** do projeto **Compraê**, utilizando o **Spring Cloud Config Server** como base para fornecer arquivos de configuração externos a todos os microserviços da aplicação.

---

## 📌 Objetivo

Centralizar, versionar e disponibilizar configurações para os microserviços da plataforma Compraê, garantindo:

- Manutenção unificada de parâmetros sensíveis e variáveis de ambiente.
- Facilidade na troca de perfis (`dev`, `prod`, etc.).
- Redução de erros de configuração entre serviços.
- Atualização dinâmica sem a necessidade de rebuild/redeploy dos serviços.

---

## 🗂️ Estrutura do Projeto

comprae-config-server/  
├── src/  
│ └── main/  
│ ├── java/  
│ │ └── com/comprae/config/  
│ └── resources/  
│ └── application.yml  
├── pom.xml  
└── README.md  


---

## ⚙️ Tecnologias e Dependências

- Java 17+ (ou superior)
- Spring Boot
- Spring Cloud Config Server
- Git remoto como fonte de configuração
- Maven

---

## 🔧 Configuração padrão (`application.yml`)

```yaml
server:
  port: 8888

spring:
  application:
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



