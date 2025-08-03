package com.configsystem.server.repositorio;

import com.configsystem.server.entidade.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para gerenciar configurações
 */
@Repository
public interface RepositorioConfiguracao extends JpaRepository<Configuracao, Long> {

    /**
     * Busca uma configuração por namespace, ambiente e chave
     */
    @Query("SELECT c FROM Configuracao c WHERE c.namespace = :namespace AND c.ambiente = :ambiente AND c.chave = :chave AND c.ativo = true")
    Optional<Configuracao> buscarPorNamespaceAmbienteEChave(
        @Param("namespace") String namespace,
        @Param("ambiente") String ambiente,
        @Param("chave") String chave
    );

    /**
     * Busca todas as configurações de um namespace e ambiente
     */
    @Query("SELECT c FROM Configuracao c WHERE c.namespace = :namespace AND c.ambiente = :ambiente AND c.ativo = true ORDER BY c.chave")
    List<Configuracao> buscarPorNamespaceEAmbiente(
        @Param("namespace") String namespace,
        @Param("ambiente") String ambiente
    );

    /**
     * Busca todas as configurações de um namespace
     */
    @Query("SELECT c FROM Configuracao c WHERE c.namespace = :namespace AND c.ativo = true ORDER BY c.ambiente, c.chave")
    List<Configuracao> buscarPorNamespace(@Param("namespace") String namespace);

    /**
     * Busca todas as configurações de um ambiente
     */
    @Query("SELECT c FROM Configuracao c WHERE c.ambiente = :ambiente AND c.ativo = true ORDER BY c.namespace, c.chave")
    List<Configuracao> buscarPorAmbiente(@Param("ambiente") String ambiente);

    /**
     * Busca configurações por chave (em todos os namespaces e ambientes)
     */
    @Query("SELECT c FROM Configuracao c WHERE c.chave = :chave AND c.ativo = true ORDER BY c.namespace, c.ambiente")
    List<Configuracao> buscarPorChave(@Param("chave") String chave);

    /**
     * Busca configurações que contenham um texto específico na chave ou valor
     */
    @Query("SELECT c FROM Configuracao c WHERE (LOWER(c.chave) LIKE LOWER(CONCAT('%', :texto, '%')) OR LOWER(c.valor) LIKE LOWER(CONCAT('%', :texto, '%'))) AND c.ativo = true ORDER BY c.namespace, c.ambiente, c.chave")
    List<Configuracao> buscarPorTexto(@Param("texto") String texto);

    /**
     * Conta o número de configurações por namespace
     */
    @Query("SELECT COUNT(c) FROM Configuracao c WHERE c.namespace = :namespace AND c.ativo = true")
    long contarPorNamespace(@Param("namespace") String namespace);

    /**
     * Lista todos os namespaces únicos
     */
    @Query("SELECT DISTINCT c.namespace FROM Configuracao c WHERE c.ativo = true ORDER BY c.namespace")
    List<String> listarNamespaces();

    /**
     * Lista todos os ambientes únicos
     */
    @Query("SELECT DISTINCT c.ambiente FROM Configuracao c WHERE c.ativo = true ORDER BY c.ambiente")
    List<String> listarAmbientes();

    /**
     * Verifica se existe uma configuração com a chave, namespace e ambiente especificados
     */
    boolean existsByChaveAndNamespaceAndAmbienteAndAtivoTrue(String chave, String namespace, String ambiente);
}
