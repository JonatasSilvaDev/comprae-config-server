package com.configsystem.server.controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.configsystem.server.repositorio.RepositorioConfiguracao;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para realizar testes de saude do Config Server
 */
@RestController
@RequestMapping("/api/v1/diagnostico")
@Tag(name = "Config Server", description = "API para gerenciar configurações centralizadas")
public class DiagnosticoController {//devo mudar o nome dessa classe para algo coerente com o que ela faz especificamente tipo StatusController.

    @Autowired
    private RepositorioConfiguracao repositorioConfiguracao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(DiagnosticoController.class);

    @Operation(summary = "Status do Config Server", description = "Retorna o status do servidor de configuração")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> statusConfigServer() {
        log.info("Verificando status do Config Server");
        
        Map<String, Object> status = new HashMap<>();
        status.put("servico", "Config Server");
        status.put("status", "UP");
        status.put("versao", "1.0.0");
        status.put("descricao", "Servidor de configuração centralizada do Compraê");
        status.put("porta", 8888);
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Informações do Config Server", description = "Retorna informações básicas sobre o servidor")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> informacoesConfigServer() {
        log.info("Solicitação para informações do Config Server");
        
        Map<String, Object> info = new HashMap<>();
        info.put("nome", "Compraê Config Server");
        info.put("descricao", "Servidor centralizado de configurações para o ecossistema Compraê");
        info.put("versao", "1.0.0");
        info.put("ambiente", "desenvolvimento");
        info.put("funcionalidades", new String[]{
            "Gerenciamento centralizado de configurações",
            "API REST para CRUD de configurações",
            "Suporte a múltiplos ambientes",
            "Cache de configurações",
            "Versionamento de configurações"
        });
        
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Health Check", description = "Verifica se o Config Server está funcionando corretamente")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.info("Health check do Config Server");
        
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Verificações básicas do sistema
            boolean sistemaOk = true;
            boolean bancoOk = validaStatusBanco();//quando eu desligo o banco, a consulta está retornando timeout no insomnia
            boolean cacheOk = validaStatusCache();
            
            boolean healthy = sistemaOk && bancoOk && cacheOk;
            
            health.put("status", healthy ? "UP" : "DOWN");
            health.put("sistema", sistemaOk ? "OK" : "ERROR");
            health.put("banco_dados", bancoOk ? "OK" : "ERROR");
            health.put("cache", cacheOk ? "OK" : "ERROR");
            health.put("timestamp", System.currentTimeMillis());
            
            return healthy ? ResponseEntity.ok(health) : ResponseEntity.status(503).body(health);
            
        } catch (Exception e) {
            log.error("Erro no health check do Config Server", e);
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            return ResponseEntity.status(503).body(health);
        }
    }

    @Operation(summary = "Listar namespaces", description = "Lista todos os namespaces de configuração disponíveis")
    @GetMapping("/namespaces")
    public ResponseEntity<Map<String, Object>> listarNamespaces() {
        log.info("Listando namespaces disponíveis");
        
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("namespaces", new String[]{
            "comprae-produto-service",
            "comprae-usuario-service", 
            "comprae-pedido-service",
            "comprae-pagamento-service",
            "comprae-notification-service"
        });
        resposta.put("total", 5);
        resposta.put("descricao", "Namespaces de configuração do ecossistema Compraê");
        
        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Listar ambientes", description = "Lista todos os ambientes disponíveis")
    @GetMapping("/ambientes")
    public ResponseEntity<Map<String, Object>> listarAmbientes() {
        log.info("Listando ambientes disponíveis");
        
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("ambientes", new String[]{"dev", "test", "prod"});
        resposta.put("ativo", "dev");
        resposta.put("descricao", "Ambientes de configuração disponíveis");
        
        return ResponseEntity.ok(resposta);
    }

    private Boolean validaStatusBanco () {
        //essa validação deve estar no proprio serviço do banco ou no repositorio
        try {
            repositorioConfiguracao.count();
            return true;
        } catch (Exception e) {            
            log.error("Erro ao verificar banco de dados", e);
            return false;
        }
    }

    private Boolean validaStatusCache() {
        //preciso de um serviço especifico pra o redis 
        //e essa classe deve ser responsavel por se auto validar
        try {
            String pong = redisTemplate
            .getConnectionFactory()
            .getConnection()
            .ping();

            return "PONG".equalsIgnoreCase(pong);
        } catch (Exception e) {
            log.error("Erro ao verificar cache Redis", e);
            return false;
        }
    }
}
