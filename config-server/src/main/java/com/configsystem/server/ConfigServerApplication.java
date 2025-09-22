package com.configsystem.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicação principal do Config Server
 * 
 * Funcionalidades habilitadas:
 * - @EnableCaching: Sistema de cache multi-layer (Caffeine + Redis)
 * - @EnableAsync: Processamento assíncrono para melhor performance
 * - @EnableScheduling: Tarefas agendadas para manutenção do cache
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
