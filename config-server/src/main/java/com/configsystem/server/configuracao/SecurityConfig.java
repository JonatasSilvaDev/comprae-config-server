package com.configsystem.server.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança simplificada para demonstração
 * Permite acesso livre a todos os endpoints do Config Server
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desabilitar CSRF para facilitar testes de API
            .csrf(AbstractHttpConfigurer::disable)
            
            // Desabilitar autenticação HTTP Basic
            .httpBasic(AbstractHttpConfigurer::disable)
            
            // Permitir acesso a todos os endpoints
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/v1/configuracoes/**").permitAll()
                .requestMatchers("/api/v1/diagnostico/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().permitAll()
            );

        return http.build();
    }
}