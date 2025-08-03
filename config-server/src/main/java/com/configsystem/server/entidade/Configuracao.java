package com.configsystem.server.entidade;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma configuração no sistema
 */
@Entity
@Table(name = "configuracoes", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"chave", "namespace", "ambiente"}))
public class Configuracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A chave é obrigatória")
    @Size(max = 255, message = "A chave deve ter no máximo 255 caracteres")
    @Column(name = "chave", nullable = false)
    private String chave;

    @NotBlank(message = "O valor é obrigatório")
    @Column(name = "valor", nullable = false, columnDefinition = "TEXT")
    private String valor;

    @NotBlank(message = "O namespace é obrigatório")
    @Size(max = 100, message = "O namespace deve ter no máximo 100 caracteres")
    @Column(name = "namespace", nullable = false)
    private String namespace;

    @NotBlank(message = "O ambiente é obrigatório")
    @Size(max = 50, message = "O ambiente deve ter no máximo 50 caracteres")
    @Column(name = "ambiente", nullable = false)
    private String ambiente;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    @Column(name = "descricao")
    private String descricao;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Version
    @Column(name = "versao")
    private Long versao;

    // Construtores
    public Configuracao() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public Configuracao(String chave, String valor, String namespace, String ambiente) {
        this();
        this.chave = chave;
        this.valor = valor;
        this.namespace = namespace;
        this.ambiente = ambiente;
    }

    // Métodos de callback do JPA
    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
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

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
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

    @Override
    public String toString() {
        return "Configuracao{" +
                "id=" + id +
                ", chave='" + chave + '\'' +
                ", valor='" + valor + '\'' +
                ", namespace='" + namespace + '\'' +
                ", ambiente='" + ambiente + '\'' +
                ", descricao='" + descricao + '\'' +
                ", criadoEm=" + criadoEm +
                ", atualizadoEm=" + atualizadoEm +
                ", ativo=" + ativo +
                ", versao=" + versao +
                '}';
    }
}
