package com.configsystem.server.dto;

public record RequisicaoConfiguracao(
    String chave,
    String valor,
    String namespace,
    String ambiente,
    String descricao
) {}
