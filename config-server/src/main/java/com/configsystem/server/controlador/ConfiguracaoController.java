package com.configsystem.server.controlador;

import com.configsystem.server.dto.RequisicaoConfiguracao;
import com.configsystem.server.entidade.Configuracao;
import com.configsystem.server.servico.ServicoConfiguracao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import java.util.Set;
import java.util.HashMap;

import java.util.List;

@RestController
@RequestMapping("/api/v1/configuracoes")
public class ConfiguracaoController {

	@Autowired
	private ServicoConfiguracao servico;

	// Listar todas as configurações
	@GetMapping
	public List<Configuracao> listarTodasConfiguracoes() {
		return servico.listarTodasConfiguracoes();
	}

	// Buscar configuração por chave
	@GetMapping("/{chave}")
	public Configuracao buscarConfiguracao(@PathVariable String chave) {
		return servico.buscarPorTexto(chave);
	}

	// // Buscar configuração por ID
	// @GetMapping("/v1/configuracoes/id/{id}")
	// public Configuracao buscarConfiguracaoPorId(@PathVariable Long id) {
	// 	return servico.buscarPorId(id);
	// }

	// Criar nova configuração
	@PostMapping
	public void criarConfiguracao(@RequestBody RequisicaoConfiguracao configuracao) {
		servico.salvarConfiguracao(
				configuracao.chave(),
				configuracao.valor(),
				configuracao.namespace(),
				configuracao.ambiente(),
				configuracao.descricao());
	}

	// Atualizar uma configuração existente
	@PutMapping("/{id}")
	public void atualizarConfiguracao(@PathVariable Long id, @RequestBody RequisicaoConfiguracao configuracao) {
		servico.salvarConfiguracao(
				configuracao.chave(),
				configuracao.valor(),
				configuracao.namespace(),
				configuracao.ambiente(),
				configuracao.descricao());
	}

	//deletar uma configuração
	@DeleteMapping("/{id}")
	public void deletarConfiguracao(@PathVariable String chave, @RequestParam String namespace, @RequestParam String ambiente) {
		servico.removerConfiguracao(chave, namespace, ambiente);
	}

	// Buscar configuração por ID
//	@GetMapping("/{id}")
//	public ResponseEntity<Configuracao> buscarPorId(@PathVariable Long id) {
//		Optional<Configuracao> config = repositorioConfiguracao.findById(id);
//		return config.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//	}
//

//
//	// Atualizar configuração existente
//	@PutMapping("/{id}")
//	public ResponseEntity<Configuracao> atualizar(@PathVariable Long id, @RequestBody Configuracao configuracao) {
//		if (!repositorioConfiguracao.existsById(id)) {
//			return ResponseEntity.notFound().build();
//		}
//		configuracao.setId(id);
//		Configuracao atualizada = repositorioConfiguracao.save(configuracao);
//		return ResponseEntity.ok(atualizada);
//	}
//
//	// Deletar configuração
//	@DeleteMapping("/{id}")
//	public ResponseEntity<Void> deletar(@PathVariable Long id) {
//		if (!repositorioConfiguracao.existsById(id)) {
//			return ResponseEntity.notFound().build();
//		}
//		repositorioConfiguracao.deleteById(id);
//		return ResponseEntity.noContent().build();
//	}

    @Value("${SPRING_PROFILES_ACTIVE}")
    private String ambiente;

	@Value("${SPRING_NAMESPACE:default}")
    private String namespace;

	// Endpoint que o SDK espera: /api/v1/configuracoes/default/dev/map
	@GetMapping("/{namespace}/{ambiente}/map")
	@ResponseBody
	public ResponseEntity<Map<String, String>> buscarMapaConfiguracoes(
			@PathVariable String namespace, 
			@PathVariable String ambiente) {
		try {
			Map<String, String> mapa = servico.buscarTodasConfiguracoes(namespace, ambiente);
			return ResponseEntity.ok(mapa);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
}
