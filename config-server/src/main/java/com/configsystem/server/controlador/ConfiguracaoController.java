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
@RequestMapping("/api")
public class ConfiguracaoController {

	@Autowired
	private ServicoConfiguracao servico;

	// Listar todas as configurações
	@GetMapping("/v1/configuracoes")
	public List<Configuracao> listarTodasConfiguracoes() {
		return servico.listarTodasConfiguracoes();
	}

	// Buscar configuração por chave
	@GetMapping("/v1/configuracoes/{chave}")
	public Configuracao buscarConfiguracao(@PathVariable String chave) {
		return servico.buscarPorTexto(chave);
	}

	// // Buscar configuração por ID
	// @GetMapping("/v1/configuracoes/id/{id}")
	// public Configuracao buscarConfiguracaoPorId(@PathVariable Long id) {
	// 	return servico.buscarPorId(id);
	// }

	// Criar nova configuração
	@PostMapping("/v1/configuracoes")
	public void criarConfiguracao(@RequestBody RequisicaoConfiguracao configuracao) {
		servico.salvarConfiguracao(
				configuracao.chave(),
				configuracao.valor(),
				configuracao.namespace(),
				configuracao.ambiente(),
				configuracao.descricao());
	}

	// Atualizar uma configuração existente
	@PutMapping("/v1/configuracoes/{id}")
	public void atualizarConfiguracao(@PathVariable Long id, @RequestBody RequisicaoConfiguracao configuracao) {
		servico.salvarConfiguracao(
				configuracao.chave(),
				configuracao.valor(),
				configuracao.namespace(),
				configuracao.ambiente(),
				configuracao.descricao());
	}

	//deletar uma configuração
	@DeleteMapping("/v1/configuracoes/{id}")
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

	// depois não esquecer de ajustar essa nojera
	@GetMapping("/configs/default/${SPRING_PROFILES_ACTIVE}/map")
	@ResponseBody
	public ResponseEntity<Map<String, String>> buscarMapaConfiguracoes() {
		try {
			Map<String, String> mapa = servico.buscarTodasConfiguracoes("default", ambiente);
			return ResponseEntity.ok(mapa);
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}

}
