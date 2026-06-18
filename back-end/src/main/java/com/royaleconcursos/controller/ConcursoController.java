package com.royaleconcursos.controller;

import com.royaleconcursos.dto.ConcursoDTO;
import com.royaleconcursos.service.ConcursoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concursos")
@CrossOrigin(origins = "*")
public class ConcursoController {

    private final ConcursoService service;

    public ConcursoController(ConcursoService service) {
        this.service = service;
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<ConcursoDTO> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ConcursoDTO>> buscar(
            @RequestParam String q,
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String tipo) {

        List<ConcursoDTO> resultado = service.buscarPorOrgao(q);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/abertos")
    public ResponseEntity<List<ConcursoDTO>> abertos() {
        return ResponseEntity.ok(service.listarPorTipo("aberto"));
    }

    @GetMapping("/previstos")
    public ResponseEntity<List<ConcursoDTO>> previstos() {
        return ResponseEntity.ok(service.listarPorTipo("previsto"));
    }
}