package com.configsystem.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para criação de novas configurações
 */
@Schema(description = "Dados para criação de uma nova configuração")
public class CreateConfigurationRequest {
    
    @NotBlank(message = "Service name é obrigatório")
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Service name deve conter apenas letras, números, hífen e underscore")
    @Size(min = 2, max = 50, message = "Service name deve ter entre 2 e 50 caracteres")
    @Schema(description = "Nome do serviço", example = "produto-service")
    private String serviceName;
    
    @NotBlank(message = "Config key é obrigatório")
    @Pattern(regexp = "^[a-zA-Z0-9-_.]+$", message = "Config key deve conter apenas letras, números, hífen, underscore e ponto")
    @Size(min = 2, max = 100, message = "Config key deve ter entre 2 e 100 caracteres")
    @Schema(description = "Chave da configuração", example = "database.url")
    private String configKey;
    
    @NotNull(message = "Config value é obrigatório")
    @Size(max = 1000, message = "Config value deve ter no máximo 1000 caracteres")
    @Schema(description = "Valor da configuração", example = "jdbc:postgresql://localhost:5432/produto")
    private String configValue;
    
    @NotBlank(message = "Environment é obrigatório")
    @Pattern(regexp = "^(dev|test|staging|prod)$", message = "Environment deve ser dev, test, staging ou prod")
    @Schema(description = "Ambiente da configuração", example = "dev")
    private String environment;
    
    @Size(max = 500, message = "Description deve ter no máximo 500 caracteres")
    @Schema(description = "Descrição da configuração", example = "URL de conexão com o banco de dados")
    private String description;
    
    // Constructors
    public CreateConfigurationRequest() {}
    
    public CreateConfigurationRequest(String serviceName, String configKey, String configValue, String environment, String description) {
        this.serviceName = serviceName;
        this.configKey = configKey;
        this.configValue = configValue;
        this.environment = environment;
        this.description = description;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "CreateConfigurationRequest{" +
                "serviceName='" + serviceName + '\'' +
                ", configKey='" + configKey + '\'' +
                ", environment='" + environment + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}