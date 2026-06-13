package com.royaleconcursos.controller;

import com.royaleconcursos.dto.RankingNotaDTO;
import com.royaleconcursos.service.RankingNotaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@CrossOrigin(origins = "*")
public class RankingNotaController {

    private final RankingNotaService service;

    public RankingNotaController(RankingNotaService service) {
        this.service = service;
    }

    // Salva nota — retorna 409 se nome duplicado
    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody RankingNotaDTO dto) {
        try {
            return ResponseEntity.ok(service.salvar(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    // Retorna ranking de um concurso ordenado por nota
    @GetMapping("/{concursoId}")
    public ResponseEntity<List<RankingNotaDTO>> listar(@PathVariable Long concursoId) {
        return ResponseEntity.ok(service.listarPorConcurso(concursoId));
    }
}