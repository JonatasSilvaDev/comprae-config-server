package com.configsystem.server.controlador;

import com.configsystem.server.dto.Configuration;
import com.configsystem.server.servico.IntelligentCacheService;
import com.configsystem.server.servico.ResilientConfigurationService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Controlador REST para Configurações com padrões arquiteturais avançados
 */
@RestController
@RequestMapping("/api/v1/configurations")
@Validated
@Tag(name = "Configuration Management", description = "APIs para gerenciamento de configurações")
public class ConfigurationController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationController.class);

    @Autowired
    private ResilientConfigurationService configurationService;

    @Autowired
    private IntelligentCacheService cacheService;

    /**
     * Busca uma configuração específica
     */
    @GetMapping("/{serviceName}/{configKey}")
    @Operation(summary = "Obter configuração", 
               description = "Busca uma configuração específica para um serviço")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Configuração encontrada"),
        @ApiResponse(responseCode = "404", description = "Configuração não encontrada"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @Timed(value = "config.get.duration", description = "Time taken to get configuration")
    public ResponseEntity<ConfigurationResponse> getConfiguration(
            @Parameter(description = "Nome do serviço")
            @PathVariable @NotBlank @Pattern(regexp = "^[a-zA-Z0-9-_]+$") String serviceName,
            
            @Parameter(description = "Chave da configuração")
            @PathVariable @NotBlank @Pattern(regexp = "^[a-zA-Z0-9-_.]+$") String configKey) {
        
        logger.info("GET /configurations/{}/{}", serviceName, configKey);
        
        try {
            Optional<String> config = configurationService.getConfiguration(serviceName, configKey);
            
            if (config.isPresent()) {
                ConfigurationResponse response = new ConfigurationResponse(
                    serviceName, configKey, config.get(), "SUCCESS"
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error getting configuration: service={}, key={}, error={}", 
                    serviceName, configKey, e.getMessage(), e);
            
            ConfigurationResponse errorResponse = new ConfigurationResponse(
                serviceName, configKey, null, "ERROR: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Busca configuração de forma assíncrona
     */
    @GetMapping("/async/{serviceName}/{configKey}")
    @Operation(summary = "Obter configuração assíncrona", 
               description = "Busca uma configuração de forma assíncrona com lazy loading")
    @Timed(value = "config.get.async.duration", description = "Time taken to get configuration async")
    public CompletableFuture<ResponseEntity<ConfigurationResponse>> getConfigurationAsync(
            @PathVariable @NotBlank String serviceName,
            @PathVariable @NotBlank String configKey) {
        
        logger.info("GET /configurations/async/{}/{}", serviceName, configKey);
        
        return cacheService.getConfigurationAsync(serviceName, configKey)
                .thenApply(config -> {
                    if (config.isPresent()) {
                        ConfigurationResponse response = new ConfigurationResponse(
                            serviceName, configKey, config.get(), "SUCCESS"
                        );
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.notFound().<ConfigurationResponse>build();
                    }
                })
                .exceptionally(ex -> {
                    logger.error("Error getting configuration async: service={}, key={}, error={}", 
                            serviceName, configKey, ex.getMessage(), ex);
                    
                    ConfigurationResponse errorResponse = new ConfigurationResponse(
                        serviceName, configKey, null, "ERROR: " + ex.getMessage()
                    );
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    /**
     * Busca todas as configurações de um serviço
     */
    @GetMapping("/{serviceName}")
    @Operation(summary = "Obter todas as configurações", 
               description = "Busca todas as configurações de um serviço específico")
    @Timed(value = "config.getall.duration", description = "Time taken to get all configurations")
    public ResponseEntity<ServiceConfigurationsResponse> getAllConfigurations(
            @PathVariable @NotBlank String serviceName) {
        
        logger.info("GET /configurations/{}", serviceName);
        
        try {
            var configurations = configurationService.getAllConfigurations(serviceName);
            
            ServiceConfigurationsResponse response = new ServiceConfigurationsResponse(
                serviceName, configurations, "SUCCESS"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting all configurations: service={}, error={}", 
                    serviceName, e.getMessage(), e);
            
            ServiceConfigurationsResponse errorResponse = new ServiceConfigurationsResponse(
                serviceName, null, "ERROR: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Cache warming endpoint
     */
    @PostMapping("/cache/warmup/{serviceName}")
    @Operation(summary = "Aquecimento de cache", 
               description = "Pré-carrega as configurações de um serviço no cache")
    public ResponseEntity<Map<String, String>> warmUpCache(
            @PathVariable @NotBlank String serviceName) {
        
        logger.info("POST /configurations/cache/warmup/{}", serviceName);
        
        try {
            cacheService.warmUpCache(serviceName);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Cache warming initiated for service: " + serviceName);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error warming up cache: service={}, error={}", 
                    serviceName, e.getMessage(), e);
            
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Cache statistics endpoint
     */
    @GetMapping("/cache/stats")
    @Operation(summary = "Estatísticas de cache", 
               description = "Retorna estatísticas detalhadas do sistema de cache")
    public ResponseEntity<Map<String, Object>> getCacheStatistics() {
        logger.info("GET /configurations/cache/stats");
        
        try {
            Map<String, Object> stats = cacheService.getCacheStatistics();
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error getting cache statistics: error={}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Verificação de saúde", 
               description = "Verifica o status de saúde do serviço de configuração")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.debug("GET /configurations/health");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("service", "config-server");
        health.put("version", "1.0.0");
        
        // Verifica circuit breaker
        boolean dbCircuitOpen = configurationService.isDatabaseCircuitBreakerOpen();
        health.put("database_circuit_breaker", dbCircuitOpen ? "OPEN" : "CLOSED");
        
        return ResponseEntity.ok(health);
    }
}

/**
 * DTO para resposta de configuração individual
 */
class ConfigurationResponse {
    private String serviceName;
    private String configKey;
    private String configValue;
    private String status;

    public ConfigurationResponse(String serviceName, String configKey, String configValue, String status) {
        this.serviceName = serviceName;
        this.configKey = configKey;
        this.configValue = configValue;
        this.status = status;
    }

    // Getters e Setters
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }
    
    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

/**
 * DTO para resposta de configurações de serviço
 */
class ServiceConfigurationsResponse {
    private String serviceName;
    private java.util.List<Configuration> configurations;
    private String status;

    public ServiceConfigurationsResponse(String serviceName, 
                                       java.util.List<Configuration> configurations, 
                                       String status) {
        this.serviceName = serviceName;
        this.configurations = configurations;
        this.status = status;
    }

    // Getters e Setters
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public java.util.List<Configuration> getConfigurations() { return configurations; }
    public void setConfigurations(java.util.List<Configuration> configurations) { 
        this.configurations = configurations; 
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}