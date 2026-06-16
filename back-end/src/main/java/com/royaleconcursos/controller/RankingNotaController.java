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

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody RankingNotaDTO dto) {
        try {
            return ResponseEntity.ok(service.salvar(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/{concursoId}")
    public ResponseEntity<List<RankingNotaDTO>> listar(@PathVariable Long concursoId) {
        return ResponseEntity.ok(service.listarPorConcurso(concursoId));
    }

    @GetMapping("/{concursoId}/pesquisar")
    public ResponseEntity<List<RankingNotaDTO>> pesquisar(
            @PathVariable Long concursoId,
            @RequestParam String nome) {
        return ResponseEntity.ok(service.pesquisar(concursoId, nome));
    }
}