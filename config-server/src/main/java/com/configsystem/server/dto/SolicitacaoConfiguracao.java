package com.configsystem.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para solicitação de criação/atualização de configuração
 */
public class SolicitacaoConfiguracao {

    @NotBlank(message = "A chave é obrigatória")
    @Size(max = 255, message = "A chave deve ter no máximo 255 caracteres")
    private String chave;

    @NotBlank(message = "O valor é obrigatório")
    private String valor;

    @NotBlank(message = "O namespace é obrigatório")
    @Size(max = 100, message = "O namespace deve ter no máximo 100 caracteres")
    private String namespace;

    @NotBlank(message = "O ambiente é obrigatório")
    @Size(max = 50, message = "O ambiente deve ter no máximo 50 caracteres")
    private String ambiente;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String descricao;

    // Construtores
    public SolicitacaoConfiguracao() {}

    public SolicitacaoConfiguracao(String chave, String valor, String namespace, String ambiente) {
        this.chave = chave;
        this.valor = valor;
        this.namespace = namespace;
        this.ambiente = ambiente;
    }

    // Getters e Setters
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

    @Override
    public String toString() {
        return "SolicitacaoConfiguracao{" +
                "chave='" + chave + '\'' +
                ", valor='" + valor + '\'' +
                ", namespace='" + namespace + '\'' +
                ", ambiente='" + ambiente + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
