-- Schema inicial para o servidor de configurações
-- Criação das tabelas principais em português conforme padrão do projeto

-- Criar tabela de configurações
CREATE TABLE configuracoes (
    id BIGSERIAL PRIMARY KEY,
    chave VARCHAR(255) NOT NULL,
    valor TEXT NOT NULL,
    namespace VARCHAR(100) NOT NULL,
    ambiente VARCHAR(50) NOT NULL,
    descricao VARCHAR(500),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    versao BIGINT NOT NULL DEFAULT 0,
    UNIQUE(chave, namespace, ambiente)
);

-- Criar tabela de histórico de configurações
CREATE TABLE historico_configuracoes (
    id BIGSERIAL PRIMARY KEY,
    chave VARCHAR(255) NOT NULL,
    valor_anterior TEXT,
    valor_novo TEXT NOT NULL,
    namespace VARCHAR(100) NOT NULL,
    ambiente VARCHAR(50) NOT NULL,
    usuario_alteracao VARCHAR(100),
    tipo_operacao VARCHAR(20) NOT NULL, -- CREATE, UPDATE, DELETE
    timestamp_alteracao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacao VARCHAR(500)
);

-- Criar índices para performance
CREATE INDEX idx_configuracoes_namespace_ambiente ON configuracoes(namespace, ambiente);
CREATE INDEX idx_configuracoes_chave ON configuracoes(chave);
CREATE INDEX idx_configuracoes_ativo ON configuracoes(ativo);
CREATE INDEX idx_configuracoes_atualizado_em ON configuracoes(atualizado_em);

CREATE INDEX idx_historico_chave_namespace_ambiente ON historico_configuracoes(chave, namespace, ambiente);
CREATE INDEX idx_historico_timestamp ON historico_configuracoes(timestamp_alteracao);
CREATE INDEX idx_historico_usuario ON historico_configuracoes(usuario_alteracao);
CREATE INDEX idx_historico_tipo_operacao ON historico_configuracoes(tipo_operacao);

-- Trigger para atualizar o campo atualizado_em automaticamente
CREATE OR REPLACE FUNCTION atualizar_timestamp_modificacao()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_atualizar_timestamp
    BEFORE UPDATE ON configuracoes
    FOR EACH ROW
    EXECUTE FUNCTION atualizar_timestamp_modificacao();

-- Inserir dados de exemplo para teste
INSERT INTO configuracoes (chave, valor, namespace, ambiente, descricao, usuario_alteracao) VALUES
('database.url', 'jdbc:postgresql://localhost:5432/comprae_dev', 'comprae-api', 'desenvolvimento', 'URL do banco de dados de desenvolvimento', 'sistema'),
('database.pool.tamanho', '10', 'comprae-api', 'desenvolvimento', 'Tamanho do pool de conexões do banco', 'sistema'),
('cache.redis.host', 'localhost', 'comprae-api', 'desenvolvimento', 'Host do servidor Redis', 'sistema'),
('cache.redis.porta', '6379', 'comprae-api', 'desenvolvimento', 'Porta do servidor Redis', 'sistema'),
('api.timeout', '5000', 'comprae-api', 'desenvolvimento', 'Timeout das requisições da API em ms', 'sistema'),
('feature.novo-checkout', 'true', 'comprae-frontend', 'desenvolvimento', 'Habilitar nova funcionalidade de checkout', 'sistema'),

('database.url', 'jdbc:postgresql://prod-db:5432/comprae_prod', 'comprae-api', 'producao', 'URL do banco de dados de produção', 'sistema'),
('database.pool.tamanho', '50', 'comprae-api', 'producao', 'Tamanho do pool de conexões do banco', 'sistema'),
('cache.redis.host', 'redis-cluster', 'comprae-api', 'producao', 'Host do cluster Redis', 'sistema'),
('cache.redis.porta', '6379', 'comprae-api', 'producao', 'Porta do cluster Redis', 'sistema'),
('api.timeout', '10000', 'comprae-api', 'producao', 'Timeout das requisições da API em ms', 'sistema'),
('feature.novo-checkout', 'false', 'comprae-frontend', 'producao', 'Habilitar nova funcionalidade de checkout', 'sistema'),

('notificacao.email.host', 'smtp.gmail.com', 'comprae-notificacao', 'desenvolvimento', 'Host do servidor de email', 'sistema'),
('notificacao.email.porta', '587', 'comprae-notificacao', 'desenvolvimento', 'Porta do servidor de email', 'sistema'),
('notificacao.email.host', 'smtp-prod.empresa.com', 'comprae-notificacao', 'producao', 'Host do servidor de email de produção', 'sistema'),
('notificacao.email.porta', '587', 'comprae-notificacao', 'producao', 'Porta do servidor de email de produção', 'sistema');
