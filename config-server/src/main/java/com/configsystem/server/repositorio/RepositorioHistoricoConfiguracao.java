package com.configsystem.server.repositorio;

import com.configsystem.server.entidade.HistoricoConfiguracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para gerenciar histórico de configurações
 */
@Repository
public interface RepositorioHistoricoConfiguracao extends JpaRepository<HistoricoConfiguracao, Long> {

    /**
     * Busca histórico por chave, namespace e ambiente
     */
    @Query("SELECT h FROM HistoricoConfiguracao h WHERE h.chave = :chave AND h.namespace = :namespace AND h.ambiente = :ambiente ORDER BY h.timestampAlteracao DESC")
    List<HistoricoConfiguracao> buscarHistoricoPorChaveNamespaceEAmbiente(
        @Param("chave") String chave,
        @Param("namespace") String namespace,
        @Param("ambiente") String ambiente
    );

    /**
     * Busca histórico por chave
     */
    @Query("SELECT h FROM HistoricoConfiguracao h WHERE h.chave = :chave ORDER BY h.timestampAlteracao DESC")
    List<HistoricoConfiguracao> buscarHistoricoPorChave(@Param("chave") String chave);

    /**
     * Busca histórico por namespace e ambiente
     */
    @Query("SELECT h FROM HistoricoConfiguracao h WHERE h.namespace = :namespace AND h.ambiente = :ambiente ORDER BY h.timestampAlteracao DESC")
    List<HistoricoConfiguracao> buscarHistoricoPorNamespaceEAmbiente(
        @Param("namespace") String namespace,
        @Param("ambiente") String ambiente
    );

    /**
     * Busca histórico por período
     */
    @Query("SELECT h FROM HistoricoConfiguracao h WHERE h.timestampAlteracao BETWEEN :inicio AND :fim ORDER BY h.timestampAlteracao DESC")
    List<HistoricoConfiguracao> buscarHistoricoPorPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    /**
     * Busca histórico por usuário
     */
    @Query("SELECT h FROM HistoricoConfiguracao h WHERE h.usuarioAlteracao = :usuario ORDER BY h.timestampAlteracao DESC")
    List<HistoricoConfiguracao> buscarHistoricoPorUsuario(@Param("usuario") String usuario);

    /**
     * Busca histórico por tipo de operação
     */
    @Query("SELECT h FROM HistoricoConfiguracao h WHERE h.tipoOperacao = :tipoOperacao ORDER BY h.timestampAlteracao DESC")
    List<HistoricoConfiguracao> buscarHistoricoPorTipoOperacao(@Param("tipoOperacao") String tipoOperacao);

    /**
     * Busca as últimas N alterações
     */
    @Query("SELECT h FROM HistoricoConfiguracao h ORDER BY h.timestampAlteracao DESC")
    List<HistoricoConfiguracao> buscarUltimasAlteracoes();

    /**
     * Conta alterações por período
     */
    @Query("SELECT COUNT(h) FROM HistoricoConfiguracao h WHERE h.timestampAlteracao BETWEEN :inicio AND :fim")
    long contarAlteracoesPorPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );

    /**
     * Remove histórico antigo (para limpeza)
     */
    @Query("DELETE FROM HistoricoConfiguracao h WHERE h.timestampAlteracao < :dataLimite")
    void removerHistoricoAntigo(@Param("dataLimite") LocalDateTime dataLimite);
}
