package com.configsystem.server.evento;

import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Listener de Eventos para Métricas e Observabilidade
 */
@Component
public class MetricsEventListener {

    private static final Logger logger = LoggerFactory.getLogger(MetricsEventListener.class);

    @Autowired
    private Counter configurationReadsCounter;

    @Autowired
    private Counter configurationUpdatesCounter;

    @Autowired
    private Counter cacheHitsCounter;

    @Autowired
    private Counter cacheMissesCounter;

    public void logConfigurationRead(String serviceName, String configKey, long responseTime) {
        logger.info("Configuration read: service={}, key={}, responseTime={}ms", 
                serviceName, configKey, responseTime);
        configurationReadsCounter.increment();
    }

    public void logConfigurationUpdate(String serviceName, String configKey, String oldValue, String newValue) {
        logger.info("Configuration updated: service={}, key={}, oldValue={}, newValue={}", 
                serviceName, configKey, oldValue, newValue);
        configurationUpdatesCounter.increment();
    }

    public void logCacheHit(String cacheName, String key) {
        logger.debug("Cache hit: cache={}, key={}", cacheName, key);
        cacheHitsCounter.increment();
    }

    public void logCacheMiss(String cacheName, String key) {
        logger.debug("Cache miss: cache={}, key={}", cacheName, key);
        cacheMissesCounter.increment();
    }
}

