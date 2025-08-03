package com.configsystem.server.servico;

import com.configsystem.server.entidade.Configuracao;
import com.configsystem.server.entidade.HistoricoConfiguracao;
import com.configsystem.server.repositorio.RepositorioConfiguracao;
import com.configsystem.server.repositorio.RepositorioHistoricoConfiguracao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço de configurações (versão em português)
 */
@Service
public class ServicoConfiguracao {

    @Autowired
    private RepositorioConfiguracao repositorioConfiguracao;

    @Autowired
    private RepositorioHistoricoConfiguracao repositorioHistorico;

    /**
     * Busca uma configuração por chave, namespace e ambiente
     */
    public String buscarConfiguracao(String chave, String namespace, String ambiente) {
        Optional<Configuracao> config = repositorioConfiguracao
            .buscarPorNamespaceAmbienteEChave(namespace, ambiente, chave);
        return config.map(Configuracao::getValor).orElse(null);
    }

    /**
     * Busca todas as configurações de um namespace e ambiente
     */
    public Map<String, String> buscarTodasConfiguracoes(String namespace, String ambiente) {
        List<Configuracao> configs = repositorioConfiguracao
            .buscarPorNamespaceEAmbiente(namespace, ambiente);
        
        return configs.stream()
            .collect(Collectors.toMap(
                Configuracao::getChave,
                Configuracao::getValor
            ));
    }

    /**
     * Salva ou atualiza uma configuração
     */
    public Configuracao salvarConfiguracao(String chave, String valor, String namespace, 
                                          String ambiente, String descricao) {
        // Buscar configuração existente
        Optional<Configuracao> configExistente = repositorioConfiguracao
            .buscarPorNamespaceAmbienteEChave(namespace, ambiente, chave);

        Configuracao configuracao;
        String valorAnterior = null;

        if (configExistente.isPresent()) {
            // Atualizar configuração existente
            configuracao = configExistente.get();
            valorAnterior = configuracao.getValor();
            configuracao.setValor(valor);
            configuracao.setDescricao(descricao);
            configuracao.setAtualizadoEm(LocalDateTime.now());
        } else {
            // Criar nova configuração
            configuracao = new Configuracao(chave, valor, namespace, ambiente);
            configuracao.setDescricao(descricao);
        }

        // Salvar configuração
        Configuracao configuracaoSalva = repositorioConfiguracao.save(configuracao);

        // Registrar no histórico
        registrarHistorico(configuracaoSalva, valorAnterior, valor);

        // Notificar alteração (log por enquanto)
        System.out.println("Configuração alterada: " + chave + " = " + valor);

        return configuracaoSalva;
    }

    /**
     * Remove uma configuração
     */
    public boolean removerConfiguracao(String chave, String namespace, String ambiente) {
        Optional<Configuracao> config = repositorioConfiguracao
            .buscarPorNamespaceAmbienteEChave(namespace, ambiente, chave);

        if (config.isPresent()) {
            Configuracao configuracao = config.get();
            String valorAnterior = configuracao.getValor();
            
            // Marcar como inativo em vez de deletar
            configuracao.setAtivo(false);
            configuracao.setAtualizadoEm(LocalDateTime.now());
            repositorioConfiguracao.save(configuracao);

            // Registrar no histórico
            registrarHistorico(configuracao, valorAnterior, null, "DELETE");

            System.out.println("Configuração removida: " + chave);
            return true;
        }
        
        return false;
    }

    /**
     * Lista todas as configurações ativas
     */
    public List<Configuracao> listarTodasConfiguracoes() {
        return repositorioConfiguracao.findAll()
            .stream()
            .filter(Configuracao::getAtivo)
            .collect(Collectors.toList());
    }

    /**
     * Busca configurações por namespace
     */
    public List<Configuracao> buscarPorNamespace(String namespace) {
        return repositorioConfiguracao.buscarPorNamespace(namespace);
    }

    /**
     * Busca histórico de uma configuração
     */
    public List<HistoricoConfiguracao> buscarHistorico(String chave, String namespace, String ambiente) {
        return repositorioHistorico.buscarHistoricoPorChaveNamespaceEAmbiente(chave, namespace, ambiente);
    }

    /**
     * Lista todos os namespaces únicos
     */
    public List<String> listarNamespaces() {
        return repositorioConfiguracao.listarNamespaces();
    }

    /**
     * Lista todos os ambientes únicos
     */
    public List<String> listarAmbientes() {
        return repositorioConfiguracao.listarAmbientes();
    }

    /**
     * Verifica se uma configuração existe
     */
    public boolean configuracaoExiste(String chave, String namespace, String ambiente) {
        return repositorioConfiguracao.existsByChaveAndNamespaceAndAmbienteAndAtivoTrue(chave, namespace, ambiente);
    }

    /**
     * Busca configurações por texto (chave ou valor)
     */
    public List<Configuracao> buscarPorTexto(String texto) {
        return repositorioConfiguracao.buscarPorTexto(texto);
    }

    /**
     * Conta configurações por namespace
     */
    public long contarPorNamespace(String namespace) {
        return repositorioConfiguracao.contarPorNamespace(namespace);
    }

    /**
     * Registra alteração no histórico
     */
    private void registrarHistorico(Configuracao config, String valorAnterior, String novoValor) {
        registrarHistorico(config, valorAnterior, novoValor, novoValor == null ? "DELETE" : (valorAnterior == null ? "CREATE" : "UPDATE"));
    }

    /**
     * Registra alteração no histórico com tipo específico
     */
    private void registrarHistorico(Configuracao config, String valorAnterior, String novoValor, String tipoOperacao) {
        HistoricoConfiguracao historico = new HistoricoConfiguracao(
            config.getChave(),
            valorAnterior,
            novoValor != null ? novoValor : config.getValor(),
            config.getNamespace(),
            config.getAmbiente(),
            tipoOperacao
        );
        
        historico.setUsuarioAlteracao("sistema"); // TODO: pegar usuário do contexto de segurança
        
        repositorioHistorico.save(historico);
    }
}
