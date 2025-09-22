package com.configsystem.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO para resposta das operações CRUD de configuração
 */
@Schema(description = "Resposta das operações de configuração")
public class ConfigurationCrudResponse {
    
    @Schema(description = "ID da configuração", example = "1")
    private Long id;
    
    @Schema(description = "Nome do serviço", example = "produto-service")
    private String serviceName;
    
    @Schema(description = "Chave da configuração", example = "database.url")
    private String configKey;
    
    @Schema(description = "Valor da configuração", example = "jdbc:postgresql://localhost:5432/produto")
    private String configValue;
    
    @Schema(description = "Ambiente da configuração", example = "dev")
    private String environment;
    
    @Schema(description = "Descrição da configuração", example = "URL de conexão com o banco de dados")
    private String description;
    
    @Schema(description = "Data de criação", example = "2025-09-22T15:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Data de última atualização", example = "2025-09-22T16:45:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Status da operação", example = "SUCCESS")
    private String status;
    
    @Schema(description = "Mensagem da operação", example = "Configuração criada com sucesso")
    private String message;
    
    // Constructors
    public ConfigurationCrudResponse() {}
    
    public ConfigurationCrudResponse(Long id, String serviceName, String configKey, String configValue, 
                                   String environment, String description, LocalDateTime createdAt, 
                                   LocalDateTime updatedAt, String status, String message) {
        this.id = id;
        this.serviceName = serviceName;
        this.configKey = configKey;
        this.configValue = configValue;
        this.environment = environment;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.message = message;
    }
    
    // Factory Methods
    public static ConfigurationCrudResponse success(Long id, String serviceName, String configKey, 
                                                  String configValue, String environment, String description,
                                                  LocalDateTime createdAt, LocalDateTime updatedAt, String message) {
        return new ConfigurationCrudResponse(id, serviceName, configKey, configValue, environment, description,
                                           createdAt, updatedAt, "SUCCESS", message);
    }
    
    public static ConfigurationCrudResponse error(String message) {
        ConfigurationCrudResponse response = new ConfigurationCrudResponse();
        response.setStatus("ERROR");
        response.setMessage(message);
        return response;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }
    
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    @Override
    public String toString() {
        return "ConfigurationCrudResponse{" +
                "id=" + id +
                ", serviceName='" + serviceName + '\'' +
                ", configKey='" + configKey + '\'' +
                ", environment='" + environment + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}