package com.configsystem.server.servico;

import com.configsystem.server.dto.Configuration;
import com.configsystem.server.evento.MetricsEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço de Cache Inteligente com estratégias avançadas
 */
@Service
public class IntelligentCacheService {

    private static final Logger logger = LoggerFactory.getLogger(IntelligentCacheService.class);

    @Autowired
    private CacheManager caffeineCacheManager;

    @Autowired
    private CacheManager redisCacheManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MetricsEventListener metricsEventListener;

    @Autowired
    private ResilientConfigurationService configurationService;

    // Estatísticas de acesso para cache inteligente
    private final Map<String, CacheStats> accessStats = new ConcurrentHashMap<>();

    /**
     * Busca valor com estratégia de cache multi-layer
     */
    public <T> Optional<T> get(String cacheName, String key, Class<T> type) {
        String fullKey = cacheName + ":" + key;
        
        // L1 Cache (Caffeine - In-Memory)
        Cache l1Cache = caffeineCacheManager.getCache(cacheName + "-l1");
        if (l1Cache != null) {
            Cache.ValueWrapper wrapper = l1Cache.get(key);
            if (wrapper != null) {
                logger.debug("L1 Cache HIT: {}", fullKey);
                metricsEventListener.logCacheHit(cacheName + "-l1", key);
                updateStats(fullKey, true);
                return Optional.of(type.cast(wrapper.get()));
            }
        }

        // L2 Cache (Redis - Distributed)
        Cache l2Cache = redisCacheManager.getCache(cacheName);
        if (l2Cache != null) {
            Cache.ValueWrapper wrapper = l2Cache.get(key);
            if (wrapper != null) {
                logger.debug("L2 Cache HIT: {}", fullKey);
                metricsEventListener.logCacheHit(cacheName, key);
                
                // Promove para L1 cache (cache warming)
                if (l1Cache != null) {
                    l1Cache.put(key, wrapper.get());
                }
                
                updateStats(fullKey, true);
                return Optional.of(type.cast(wrapper.get()));
            }
        }

        logger.debug("Cache MISS: {}", fullKey);
        metricsEventListener.logCacheMiss(cacheName, key);
        updateStats(fullKey, false);
        
        return Optional.empty();
    }

    /**
     * Armazena valor em ambos os layers de cache
     */
    public void put(String cacheName, String key, Object value) {
        String fullKey = cacheName + ":" + key;
        logger.debug("Caching value: {}", fullKey);

        // L1 Cache (Caffeine)
        Cache l1Cache = caffeineCacheManager.getCache(cacheName + "-l1");
        if (l1Cache != null) {
            l1Cache.put(key, value);
        }

        // L2 Cache (Redis)
        Cache l2Cache = redisCacheManager.getCache(cacheName);
        if (l2Cache != null) {
            l2Cache.put(key, value);
        }
    }

    /**
     * Lazy Loading - carrega dados apenas quando necessário
     */
    @Async
    public CompletableFuture<Optional<String>> getConfigurationAsync(String serviceName, String configKey) {
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = serviceName + ":" + configKey;
            
            // Tenta buscar no cache primeiro
            Optional<String> cached = get("configurations", cacheKey, String.class);
            if (cached.isPresent()) {
                return cached;
            }
            
            // Se não encontrou no cache, busca na base de dados
            Optional<String> fromDb = configurationService.getConfiguration(serviceName, configKey);
            
            // Armazena no cache para próximas consultas
            if (fromDb.isPresent()) {
                put("configurations", cacheKey, fromDb.get());
            }
            
            return fromDb;
        });
    }

    /**
     * Pre-loading de configurações mais acessadas
     */
    @Scheduled(fixedRate = 300000) // A cada 5 minutos
    public void preloadHotConfigurations() {
        logger.debug("Starting hot configurations preload");
        
        // Identifica as configurações mais acessadas
        List<String> hotKeys = getHotKeys();
        
        for (String hotKey : hotKeys) {
            String[] parts = hotKey.split(":", 2);
            if (parts.length == 2) {
                String serviceName = parts[0];
                String configKey = parts[1];
                
                // Verifica se ainda está no cache
                Optional<String> cached = get("configurations", hotKey, String.class);
                if (cached.isEmpty()) {
                    // Recarrega no cache
                    configurationService.getConfiguration(serviceName, configKey)
                        .ifPresent(value -> put("configurations", hotKey, value));
                }
            }
        }
        
        logger.debug("Completed hot configurations preload for {} keys", hotKeys.size());
    }

    /**
     * Cache warming - pré-aquece o cache com dados importantes
     */
    @Async
    public void warmUpCache(String serviceName) {
        logger.info("Warming up cache for service: {}", serviceName);
        
        try {
            List<Configuration> allConfigs = configurationService.getAllConfigurations(serviceName);
            
            for (Configuration config : allConfigs) {
                String cacheKey = serviceName + ":" + config.getConfigKey();
                put("configurations", cacheKey, config.getConfigValue());
            }
            
            logger.info("Cache warmed up for service: {} with {} configurations", 
                    serviceName, allConfigs.size());
            
        } catch (Exception e) {
            logger.error("Error warming up cache for service: {}, error: {}", 
                    serviceName, e.getMessage());
        }
    }

    /**
     * Invalida cache de forma inteligente
     */
    public void evict(String cacheName, String key) {
        logger.debug("Evicting cache: {}:{}", cacheName, key);
        
        // Remove de ambos os layers
        Cache l1Cache = caffeineCacheManager.getCache(cacheName + "-l1");
        if (l1Cache != null) {
            l1Cache.evict(key);
        }
        
        Cache l2Cache = redisCacheManager.getCache(cacheName);
        if (l2Cache != null) {
            l2Cache.evict(key);
        }
        
        // Remove das estatísticas
        accessStats.remove(cacheName + ":" + key);
    }

    /**
     * Limpa todo o cache
     */
    public void evictAll(String cacheName) {
        logger.info("Evicting all cache: {}", cacheName);
        
        Cache l1Cache = caffeineCacheManager.getCache(cacheName + "-l1");
        if (l1Cache != null) {
            l1Cache.clear();
        }
        
        Cache l2Cache = redisCacheManager.getCache(cacheName);
        if (l2Cache != null) {
            l2Cache.clear();
        }
        
        // Remove estatísticas relacionadas
        accessStats.entrySet().removeIf(entry -> entry.getKey().startsWith(cacheName + ":"));
    }

    /**
     * Retorna estatísticas do cache
     */
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Estatísticas gerais
        int totalHits = accessStats.values().stream().mapToInt(CacheStats::getHits).sum();
        int totalMisses = accessStats.values().stream().mapToInt(CacheStats::getMisses).sum();
        double hitRatio = totalHits + totalMisses > 0 ? (double) totalHits / (totalHits + totalMisses) : 0;
        
        stats.put("totalHits", totalHits);
        stats.put("totalMisses", totalMisses);
        stats.put("hitRatio", hitRatio);
        stats.put("totalKeys", accessStats.size());
        
        return stats;
    }

    /**
     * Identifica as chaves mais acessadas (hot keys)
     */
    private List<String> getHotKeys() {
        return accessStats.entrySet().stream()
                .filter(entry -> entry.getValue().getHits() > 10) // Threshold configurável
                .sorted((e1, e2) -> Integer.compare(e2.getValue().getHits(), e1.getValue().getHits()))
                .limit(50) // Top 50
                .map(Map.Entry::getKey)
                .toList();
    }

    /**
     * Atualiza estatísticas de acesso
     */
    private void updateStats(String key, boolean hit) {
        accessStats.computeIfAbsent(key, k -> new CacheStats()).update(hit);
    }

    /**
     * Classe para estatísticas de cache
     */
    private static class CacheStats {
        private int hits = 0;
        private int misses = 0;
        private LocalDateTime lastAccess = LocalDateTime.now();

        public void update(boolean hit) {
            if (hit) {
                hits++;
            } else {
                misses++;
            }
            lastAccess = LocalDateTime.now();
        }

        public int getHits() { return hits; }
        public int getMisses() { return misses; }
        public LocalDateTime getLastAccess() { return lastAccess; }
    }
}