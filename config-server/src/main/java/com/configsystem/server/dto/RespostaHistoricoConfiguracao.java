package com.configsystem.server.dto;

import com.configsystem.server.entidade.HistoricoConfiguracao;

import java.time.LocalDateTime;

/**
 * DTO de resposta para histórico de configurações
 */
public class RespostaHistoricoConfiguracao {

    private Long id;
    private String chave;
    private String valorAnterior;
    private String valorNovo;
    private String namespace;
    private String ambiente;
    private String tipoOperacao;
    private String usuarioAlteracao;
    private LocalDateTime timestampAlteracao;
    private String observacao;

    // Construtores
    public RespostaHistoricoConfiguracao() {}

    public RespostaHistoricoConfiguracao(HistoricoConfiguracao historico) {
        this.id = historico.getId();
        this.chave = historico.getChave();
        this.valorAnterior = historico.getValorAnterior();
        this.valorNovo = historico.getValorNovo();
        this.namespace = historico.getNamespace();
        this.ambiente = historico.getAmbiente();
        this.tipoOperacao = historico.getTipoOperacao();
        this.usuarioAlteracao = historico.getUsuarioAlteracao();
        this.timestampAlteracao = historico.getTimestampAlteracao();
        this.observacao = historico.getObservacao();
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

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(String tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public String getUsuarioAlteracao() {
        return usuarioAlteracao;
    }

    public void setUsuarioAlteracao(String usuarioAlteracao) {
        this.usuarioAlteracao = usuarioAlteracao;
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
        return "RespostaHistoricoConfiguracao{" +
                "id=" + id +
                ", chave='" + chave + '\'' +
                ", tipoOperacao='" + tipoOperacao + '\'' +
                ", usuarioAlteracao='" + usuarioAlteracao + '\'' +
                ", timestampAlteracao=" + timestampAlteracao +
                ", namespace='" + namespace + '\'' +
                ", ambiente='" + ambiente + '\'' +
                '}';
    }
}
