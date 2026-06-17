package com.royaleconcursos.controller;

import com.royaleconcursos.dto.AnuncioDTO;
import com.royaleconcursos.model.Anuncio;
import com.royaleconcursos.service.AnuncioService;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AnuncioController {
    
    @Autowired
    private AnuncioService anuncioService;

    @GetMapping("/anuncios")
    public ResponseEntity<List<Anuncio>> listar () {
        return ResponseEntity.ok(anuncioService.listarAtivos());
    }

    // adimin crir anuncios
    @PostMapping("/admin/anuncios")
    public ResponseEntity<Anuncio> criar (@RequestBody AnuncioDTO dto) {
        return ResponseEntity.ok(anuncioService.criar(dto));
    }

    // ativar/desativar
    @PatchMapping("/admin/anuncios/{id}/toggle")
    public ResponseEntity<Void> toggle (@PathVariable Long id) {
        anuncioService.toggleAtivo(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/adimin/anuncios/{id}")
    public ResponseEntity<Void> deletar (@PathVariable Long id) {
        anuncioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    
}
