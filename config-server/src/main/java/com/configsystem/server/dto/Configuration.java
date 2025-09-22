package com.configsystem.server.dto;

import java.sql.Timestamp;

/**
 * DTO para Configuração
 */
public class Configuration {
    private String serviceName;
    private String configKey;
    private String configValue;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Configuration(String serviceName, String configKey, String configValue, 
                        String description, Timestamp createdAt, Timestamp updatedAt) {
        this.serviceName = serviceName;
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters e Setters
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }
    
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}