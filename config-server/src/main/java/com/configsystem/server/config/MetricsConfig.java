package com.configsystem.server.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Configuração de Métricas Customizadas para Observabilidade
 */
@Configuration
public class MetricsConfig {

    private final AtomicInteger activeConfigurations = new AtomicInteger(0);
    private final AtomicInteger cacheHits = new AtomicInteger(0);
    private final AtomicInteger cacheMisses = new AtomicInteger(0);

    @Bean
    public Counter configurationReadsCounter(MeterRegistry meterRegistry) {
        return Counter.builder("config.reads.total")
                .description("Total number of configuration reads")
                .tag("service", "config-server")
                .register(meterRegistry);
    }

    @Bean
    public Counter configurationUpdatesCounter(MeterRegistry meterRegistry) {
        return Counter.builder("config.updates.total")
                .description("Total number of configuration updates")
                .tag("service", "config-server")
                .register(meterRegistry);
    }

    @Bean
    public Timer configurationQueryTimer(MeterRegistry meterRegistry) {
        return Timer.builder("config.query.duration")
                .description("Time taken to query configurations")
                .tag("service", "config-server")
                .register(meterRegistry);
    }

    @Bean
    public Counter cacheHitsCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cache.hits.total")
                .description("Total cache hits")
                .tag("cache", "configurations")
                .register(meterRegistry);
    }

    @Bean
    public Counter cacheMissesCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cache.misses.total")
                .description("Total cache misses")
                .tag("cache", "configurations")
                .register(meterRegistry);
    }

    // Métricas básicas - Gauges serão implementadas via @EventListener se necessário

    // Getters para incrementar contadores via eventos
    public AtomicInteger getActiveConfigurations() {
        return activeConfigurations;
    }

    public AtomicInteger getCacheHits() {
        return cacheHits;
    }

    public AtomicInteger getCacheMisses() {
        return cacheMisses;
    }
}