package com.configsystem.server.entidade;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Entidade que representa o histórico de alterações de configurações
 */
@Entity
@Table(name = "historico_configuracoes")
public class HistoricoConfiguracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A chave é obrigatória")
    @Size(max = 255, message = "A chave deve ter no máximo 255 caracteres")
    @Column(name = "chave", nullable = false)
    private String chave;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @NotBlank(message = "O novo valor é obrigatório")
    @Column(name = "valor_novo", nullable = false, columnDefinition = "TEXT")
    private String valorNovo;

    @NotBlank(message = "O namespace é obrigatório")
    @Size(max = 100, message = "O namespace deve ter no máximo 100 caracteres")
    @Column(name = "namespace", nullable = false)
    private String namespace;

    @NotBlank(message = "O ambiente é obrigatório")
    @Size(max = 50, message = "O ambiente deve ter no máximo 50 caracteres")
    @Column(name = "ambiente", nullable = false)
    private String ambiente;

    @Size(max = 100, message = "O usuário deve ter no máximo 100 caracteres")
    @Column(name = "usuario_alteracao")
    private String usuarioAlteracao;

    @NotBlank(message = "O tipo de operação é obrigatório")
    @Size(max = 20, message = "O tipo de operação deve ter no máximo 20 caracteres")
    @Column(name = "tipo_operacao", nullable = false)
    private String tipoOperacao; // CREATE, UPDATE, DELETE

    @Column(name = "timestamp_alteracao", nullable = false)
    private LocalDateTime timestampAlteracao;

    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres")
    @Column(name = "observacao")
    private String observacao;

    // Construtores
    public HistoricoConfiguracao() {
        this.timestampAlteracao = LocalDateTime.now();
    }

    public HistoricoConfiguracao(String chave, String valorAnterior, String valorNovo, 
                                String namespace, String ambiente, String tipoOperacao) {
        this();
        this.chave = chave;
        this.valorAnterior = valorAnterior;
        this.valorNovo = valorNovo;
        this.namespace = namespace;
        this.ambiente = ambiente;
        this.tipoOperacao = tipoOperacao;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(String valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public String getValorNovo() {
        return valorNovo;
    }

    public void setValorNovo(String valorNovo) {
        this.valorNovo = valorNovo;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(String usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
    }

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(String tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public LocalDateTime getTimestampAlteracao() {
        return timestampAlteracao;
    }

    public void setTimestampAlteracao(LocalDateTime timestampAlteracao) {
        this.timestampAlteracao = timestampAlteracao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String toString() {
        return "HistoricoConfiguracao{" +
                "id=" + id +
                ", chave='" + chave + '\'' +
                ", valorAnterior='" + valorAnterior + '\'' +
                ", valorNovo='" + valorNovo + '\'' +
                ", namespace='" + namespace + '\'' +
                ", ambiente='" + ambiente + '\'' +
                ", usuarioAlteracao='" + usuarioAlteracao + '\'' +
                ", tipoOperacao='" + tipoOperacao + '\'' +
                ", timestampAlteracao=" + timestampAlteracao +
                ", observacao='" + observacao + '\'' +
                '}';
    }
}
