package com.configsystem.server.dto;

import com.configsystem.server.entidade.Configuracao;

/**
 * Classe de resposta para transferência de dados de configuração
 */
public class RespostaConfiguracao {
    
    private Long id;
    private String chave;
    private String valor;
    private String namespace;
    private String ambiente;
    private String descricao;
    private String criadoEm;
    private String atualizadoEm;
    private Boolean ativo;
    private Long versao;
    
    // Construtor vazio
    public RespostaConfiguracao() {}
    
    // Construtor que converte de Configuracao
    public RespostaConfiguracao(Configuracao configuracao) {
        this.id = configuracao.getId();
        this.chave = configuracao.getChave();
        this.valor = configuracao.getValor();
        this.namespace = configuracao.getNamespace();
        this.ambiente = configuracao.getAmbiente();
        this.descricao = configuracao.getDescricao();
        this.criadoEm = configuracao.getCriadoEm() != null ? 
            configuracao.getCriadoEm().toString() : null;
        this.atualizadoEm = configuracao.getAtualizadoEm() != null ? 
            configuracao.getAtualizadoEm().toString() : null;
        this.ativo = configuracao.getAtivo();
        this.versao = configuracao.getVersao();
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
    
    public String getValor() {
        return valor;
    }
    
    public void setValor(String valor) {
        this.valor = valor;
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
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getCriadoEm() {
        return criadoEm;
    }
    
    public void setCriadoEm(String criadoEm) {
        this.criadoEm = criadoEm;
    }
    
    public String getAtualizadoEm() {
        return atualizadoEm;
    }
    
    public void setAtualizadoEm(String atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    public Long getVersao() {
        return versao;
    }
    
    public void setVersao(Long versao) {
        this.versao = versao;
    }
}
