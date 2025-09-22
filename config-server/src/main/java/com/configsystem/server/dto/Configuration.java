package com.configsystem.server.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * DTO para Configuração
 */
public class Configuration {
    private Long id;
    private String serviceName;
    private String configKey;
    private String configValue;
    private String description;
    private String environment;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructor for backward compatibility
    public Configuration(String serviceName, String configKey, String configValue, 
                        String description, Timestamp createdAt, Timestamp updatedAt) {
        this.serviceName = serviceName;
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor for CRUD operations
    public Configuration(String serviceName, String configKey, String configValue, 
                        String description, String environment, Long id) {
        this.serviceName = serviceName;
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
        this.environment = environment;
        this.id = id;
    }

    // Constructor completo
    public Configuration(Long id, String serviceName, String configKey, String configValue, 
                        String description, String environment, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.serviceName = serviceName;
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
        this.environment = environment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }
    
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    // Métodos de conveniência para LocalDateTime
    public LocalDateTime getCreatedAtAsLocalDateTime() {
        return createdAt != null ? createdAt.toLocalDateTime() : null;
    }
    
    public LocalDateTime getUpdatedAtAsLocalDateTime() {
        return updatedAt != null ? updatedAt.toLocalDateTime() : null;
    }
}