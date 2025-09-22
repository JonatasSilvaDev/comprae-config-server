package com.configsystem.server.servico;

import com.configsystem.server.dto.Configuration;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de Configuração com Circuit Breaker e Retry Patterns
 */
@Service
public class ResilientConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(ResilientConfigurationService.class);

    @Autowired
    private DataSource dataSource;

    /**
     * Busca configuração com Circuit Breaker e Retry
     * Se o database falhar, usa fallback
     */
    @CircuitBreaker(name = "database", fallbackMethod = "getConfigurationFallback")
    @Retry(name = "database")
    @Cacheable(value = "configurations-l1", key = "#serviceName + ':' + #configKey")
    public Optional<String> getConfiguration(String serviceName, String configKey) {
        logger.debug("Fetching configuration: service={}, key={}", serviceName, configKey);
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT config_value FROM configurations WHERE service_name = ? AND config_key = ? AND active = true")) {
            
            stmt.setString(1, serviceName);
            stmt.setString(2, configKey);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String value = rs.getString("config_value");
                    logger.debug("Configuration found: service={}, key={}, value={}", 
                            serviceName, configKey, value);
                    return Optional.of(value);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error fetching configuration: service={}, key={}, error={}", 
                    serviceName, configKey, e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
        
        logger.debug("Configuration not found: service={}, key={}", serviceName, configKey);
        return Optional.empty();
    }

    /**
     * Busca todas as configurações de um serviço
     */
    @CircuitBreaker(name = "database", fallbackMethod = "getAllConfigurationsFallback")
    @Retry(name = "database")
    @Cacheable(value = "services-l1", key = "#serviceName")
    public List<Configuration> getAllConfigurations(String serviceName) {
        logger.debug("Fetching all configurations for service: {}", serviceName);
        
        List<Configuration> configurations = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT config_key, config_value, description, created_at, updated_at " +
                 "FROM configurations WHERE service_name = ? AND active = true")) {
            
            stmt.setString(1, serviceName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Configuration config = new Configuration(
                        serviceName,
                        rs.getString("config_key"),
                        rs.getString("config_value"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    );
                    configurations.add(config);
                }
            }
            
        } catch (Exception e) {
            logger.error("Error fetching configurations for service: {}, error={}", 
                    serviceName, e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
        
        logger.debug("Found {} configurations for service: {}", configurations.size(), serviceName);
        return configurations;
    }

    /**
     * Fallback method para getConfiguration
     */
    public Optional<String> getConfigurationFallback(String serviceName, String configKey, Exception ex) {
        logger.warn("Using fallback for configuration: service={}, key={}, reason={}", 
                serviceName, configKey, ex.getMessage());
        
        // Retorna configurações padrão baseadas no contexto
        return getDefaultConfiguration(serviceName, configKey);
    }

    /**
     * Fallback method para getAllConfigurations
     */
    public List<Configuration> getAllConfigurationsFallback(String serviceName, Exception ex) {
        logger.warn("Using fallback for all configurations: service={}, reason={}", 
                serviceName, ex.getMessage());
        
        // Retorna configurações mínimas para manter o serviço funcionando
        return getDefaultConfigurations(serviceName);
    }

    /**
     * Configurações padrão em caso de falha
     */
    private Optional<String> getDefaultConfiguration(String serviceName, String configKey) {
        // Configurações críticas que devem sempre estar disponíveis
        switch (configKey) {
            case "database.timeout":
                return Optional.of("30000");
            case "cache.ttl":
                return Optional.of("3600");
            case "retry.max-attempts":
                return Optional.of("3");
            case "circuit-breaker.failure-threshold":
                return Optional.of("50");
            default:
                return Optional.empty();
        }
    }

    /**
     * Configurações mínimas padrão
     */
    private List<Configuration> getDefaultConfigurations(String serviceName) {
        List<Configuration> defaults = new ArrayList<>();
        
        defaults.add(new Configuration(null, serviceName, "database.timeout", "30000", 
                "Default database timeout", "dev", null, null));
        defaults.add(new Configuration(null, serviceName, "cache.ttl", "3600", 
                "Default cache TTL", "dev", null, null));
        defaults.add(new Configuration(null, serviceName, "retry.max-attempts", "3", 
                "Default retry attempts", "dev", null, null));
        
        return defaults;
    }

    /**
     * Cria uma nova configuração
     */
    @CircuitBreaker(name = "database", fallbackMethod = "createConfigurationFallback")
    @Retry(name = "database")
    public Optional<Configuration> createConfiguration(String serviceName, String configKey, String configValue, String environment, String description) {
        logger.info("Creating configuration: service={}, key={}, environment={}", serviceName, configKey, environment);
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(
                 "SELECT id FROM configurations WHERE service_name = ? AND config_key = ? AND environment = ?")) {
            
            // Verifica se já existe
            checkStmt.setString(1, serviceName);
            checkStmt.setString(2, configKey);
            checkStmt.setString(3, environment);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    logger.warn("Configuration already exists: service={}, key={}, environment={}", serviceName, configKey, environment);
                    return Optional.empty(); // Já existe
                }
            }
            
            // Criar nova configuração
            try (PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO configurations (service_name, config_key, config_value, environment, description, active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, true, NOW(), NOW())",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
                
                insertStmt.setString(1, serviceName);
                insertStmt.setString(2, configKey);
                insertStmt.setString(3, configValue);
                insertStmt.setString(4, environment);
                insertStmt.setString(5, description);
                
                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            Long id = generatedKeys.getLong(1);
                            logger.info("Configuration created successfully: id={}, service={}, key={}", id, serviceName, configKey);
                            return Optional.of(new Configuration(serviceName, configKey, configValue, description, environment, id));
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Failed to create configuration: service={}, key={}, error={}", serviceName, configKey, e.getMessage(), e);
            throw new RuntimeException("Failed to create configuration", e);
        }
        
        return Optional.empty();
    }

    /**
     * Atualiza uma configuração existente
     */
    @CircuitBreaker(name = "database", fallbackMethod = "updateConfigurationFallback")
    @Retry(name = "database")
    public Optional<Configuration> updateConfiguration(String serviceName, String configKey, String environment, String configValue, String description) {
        logger.info("Updating configuration: service={}, key={}, environment={}", serviceName, configKey, environment);
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE configurations SET config_value = ?, description = ?, updated_at = NOW() WHERE service_name = ? AND config_key = ? AND environment = ? AND active = true")) {
            
            stmt.setString(1, configValue);
            stmt.setString(2, description);
            stmt.setString(3, serviceName);
            stmt.setString(4, configKey);
            stmt.setString(5, environment);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Configuration updated successfully: service={}, key={}, environment={}", serviceName, configKey, environment);
                
                // Busca a configuração atualizada
                try (PreparedStatement selectStmt = conn.prepareStatement(
                    "SELECT id, created_at, updated_at FROM configurations WHERE service_name = ? AND config_key = ? AND environment = ? AND active = true")) {
                    
                    selectStmt.setString(1, serviceName);
                    selectStmt.setString(2, configKey);
                    selectStmt.setString(3, environment);
                    
                    try (ResultSet rs = selectStmt.executeQuery()) {
                        if (rs.next()) {
                            Long id = rs.getLong("id");
                            return Optional.of(new Configuration(serviceName, configKey, configValue, description, environment, id));
                        }
                    }
                }
            } else {
                logger.warn("Configuration not found for update: service={}, key={}, environment={}", serviceName, configKey, environment);
            }
            
        } catch (Exception e) {
            logger.error("Failed to update configuration: service={}, key={}, environment={}, error={}", serviceName, configKey, environment, e.getMessage(), e);
            throw new RuntimeException("Failed to update configuration", e);
        }
        
        return Optional.empty();
    }

    /**
     * Remove uma configuração (soft delete)
     */
    @CircuitBreaker(name = "database", fallbackMethod = "deleteConfigurationFallback")
    @Retry(name = "database")
    public boolean deleteConfiguration(String serviceName, String configKey, String environment) {
        logger.info("Deleting configuration: service={}, key={}, environment={}", serviceName, configKey, environment);
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE configurations SET active = false, updated_at = NOW() WHERE service_name = ? AND config_key = ? AND environment = ? AND active = true")) {
            
            stmt.setString(1, serviceName);
            stmt.setString(2, configKey);
            stmt.setString(3, environment);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Configuration deleted successfully: service={}, key={}, environment={}", serviceName, configKey, environment);
                return true;
            } else {
                logger.warn("Configuration not found for deletion: service={}, key={}, environment={}", serviceName, configKey, environment);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Failed to delete configuration: service={}, key={}, environment={}, error={}", serviceName, configKey, environment, e.getMessage(), e);
            throw new RuntimeException("Failed to delete configuration", e);
        }
    }

    // Fallback methods
    public Optional<Configuration> createConfigurationFallback(String serviceName, String configKey, String configValue, String environment, String description, Exception ex) {
        logger.error("Create configuration fallback triggered: service={}, key={}, error={}", serviceName, configKey, ex.getMessage());
        return Optional.empty();
    }

    public Optional<Configuration> updateConfigurationFallback(String serviceName, String configKey, String environment, String configValue, String description, Exception ex) {
        logger.error("Update configuration fallback triggered: service={}, key={}, environment={}, error={}", serviceName, configKey, environment, ex.getMessage());
        return Optional.empty();
    }

    public boolean deleteConfigurationFallback(String serviceName, String configKey, String environment, Exception ex) {
        logger.error("Delete configuration fallback triggered: service={}, key={}, environment={}, error={}", serviceName, configKey, environment, ex.getMessage());
        return false;
    }

    /**
     * Verifica se o circuit breaker está aberto
     */
    public boolean isDatabaseCircuitBreakerOpen() {
        // Esta implementação seria feita com Resilience4j registry
        // Por simplicidade, retornando false
        return false;
    }
}

