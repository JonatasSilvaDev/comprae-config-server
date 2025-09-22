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
        
        defaults.add(new Configuration(serviceName, "database.timeout", "30000", 
                "Default database timeout", null, null));
        defaults.add(new Configuration(serviceName, "cache.ttl", "3600", 
                "Default cache TTL", null, null));
        defaults.add(new Configuration(serviceName, "retry.max-attempts", "3", 
                "Default retry attempts", null, null));
        
        return defaults;
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

