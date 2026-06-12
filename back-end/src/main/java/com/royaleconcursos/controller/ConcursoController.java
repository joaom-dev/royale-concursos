package com.royaleconcursos.controller;

import com.royaleconcursos.dto.ConcursoDTO;
import com.royaleconcursos.service.ConcursoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concursos")
@CrossOrigin(origins = "*") // troque "*" pelo seu domínio em produção
public class ConcursoController {

    private final ConcursoService service;

    public ConcursoController(ConcursoService service) {
        this.service = service;
    }

    // GET /api/concursos
    // Retorna todos os concursos, com filtros opcionais de uf e tipo
    @GetMapping
    public ResponseEntity<List<ConcursoDTO>> listar(
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String tipo) {

        List<ConcursoDTO> resultado;

        boolean temUf   = uf   != null && !uf.isBlank();
        boolean temTipo = tipo != null && !tipo.isBlank();

        if (temUf && temTipo) {
            resultado = service.listarPorUfETipo(uf, tipo);
        } else if (temUf) {
            resultado = service.listarPorUf(uf);
        } else if (temTipo) {
            resultado = service.listarPorTipo(tipo);
        } else {
            resultado = service.listarTodos();
        }

        return ResponseEntity.ok(resultado);
    }

    // GET /api/concursos/{id}
    // Retorna os detalhes completos de um concurso pelo ID
    // Usado pela tela de detalhes do front-end
    @GetMapping("/{id}")
    public ResponseEntity<ConcursoDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/concursos/buscar?q=federal
    // Busca por texto no nome do órgão (usada pela barra de pesquisa do front)
    @GetMapping("/buscar")
    public ResponseEntity<List<ConcursoDTO>> buscar(
            @RequestParam String q,
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String tipo) {

        List<ConcursoDTO> resultado = service.buscarPorOrgao(q);
        return ResponseEntity.ok(resultado);
    }

    // GET /api/concursos/abertos
    // Atalho para retornar só os concursos com inscrições abertas
    @GetMapping("/abertos")
    public ResponseEntity<List<ConcursoDTO>> abertos() {
        return ResponseEntity.ok(service.listarPorTipo("aberto"));
    }

    // GET /api/concursos/previstos
    // Atalho para retornar só os concursos previstos
    @GetMapping("/previstos")
    public ResponseEntity<List<ConcursoDTO>> previstos() {
        return ResponseEntity.ok(service.listarPorTipo("previsto"));
    }
}