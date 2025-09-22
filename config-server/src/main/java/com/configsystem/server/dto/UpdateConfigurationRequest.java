package com.configsystem.server.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para atualização de configurações existentes
 */
@Schema(description = "Dados para atualização de uma configuração existente")
public class UpdateConfigurationRequest {
    
    @NotNull(message = "Config value é obrigatório")
    @Size(max = 1000, message = "Config value deve ter no máximo 1000 caracteres")
    @Schema(description = "Novo valor da configuração", example = "jdbc:postgresql://localhost:5432/produto_v2")
    private String configValue;
    
    @Size(max = 500, message = "Description deve ter no máximo 500 caracteres")
    @Schema(description = "Nova descrição da configuração", example = "URL de conexão com o banco de dados atualizada")
    private String description;
    
    // Constructors
    public UpdateConfigurationRequest() {}
    
    public UpdateConfigurationRequest(String configValue, String description) {
        this.configValue = configValue;
        this.description = description;
    }
    
    // Getters and Setters
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return "UpdateConfigurationRequest{" +
                "configValue='" + configValue + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}