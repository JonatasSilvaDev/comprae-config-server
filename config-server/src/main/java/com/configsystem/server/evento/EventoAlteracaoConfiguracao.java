package com.configsystem.server.evento;

import java.time.LocalDateTime;

/**
 * Evento disparado quando uma configuração é alterada
 */
public class EventoAlteracaoConfiguracao {
    
    private String chave;
    private String valorAnterior;
    private String novoValor;
    private String namespace;
    private String ambiente;
    private String tipoOperacao; // CREATE, UPDATE, DELETE
    private LocalDateTime timestamp;
    private String usuario;
    private String origem;

    public EventoAlteracaoConfiguracao() {
        this.timestamp = LocalDateTime.now();
    }

    public EventoAlteracaoConfiguracao(String chave, String valorAnterior, String novoValor, 
                                     String namespace, String ambiente, String tipoOperacao) {
        this();
        this.chave = chave;
        this.valorAnterior = valorAnterior;
        this.novoValor = novoValor;
        this.namespace = namespace;
        this.ambiente = ambiente;
        this.tipoOperacao = tipoOperacao;
    }

    // Getters e Setters
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

    public String getNovoValor() {
        return novoValor;
    }

    public void setNovoValor(String novoValor) {
        this.novoValor = novoValor;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    @Override
    public String toString() {
        return "EventoAlteracaoConfiguracao{" +
                "chave='" + chave + '\'' +
                ", valorAnterior='" + valorAnterior + '\'' +
                ", novoValor='" + novoValor + '\'' +
                ", namespace='" + namespace + '\'' +
                ", ambiente='" + ambiente + '\'' +
                ", tipoOperacao='" + tipoOperacao + '\'' +
                ", timestamp=" + timestamp +
                ", usuario='" + usuario + '\'' +
                ", origem='" + origem + '\'' +
                '}';
    }
}
