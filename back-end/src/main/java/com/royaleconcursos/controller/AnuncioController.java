package com.royaleconcursos.controller;

import com.royaleconcursos.dto.AnuncioDTO;
import com.royaleconcursos.model.Anuncio;
import com.royaleconcursos.service.AnuncioService;
import com.royaleconcursos.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class AnuncioController {

    @Autowired
    private AnuncioService anuncioService;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/anuncios")
    public ResponseEntity<List<Anuncio>> listar() {
        return ResponseEntity.ok(anuncioService.listarAtivos());
    }

    // Agora recebe multipart/form-data: os campos do anúncio + o arquivo de imagem juntos.
    @PostMapping(value = "/admin/anuncios", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Anuncio> criar(
            @RequestParam String titulo,
            @RequestParam(required = false) String descricao,
            @RequestParam(required = false) String linkDestino,
            @RequestParam String posicao,
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataFim,
            @RequestParam(required = false, defaultValue = "true") boolean ativo,
            @RequestParam("imagem") MultipartFile imagem) {

        // 1. Salva o arquivo físico e recebe a URL relativa (ex: /fotos/anuncios/uuid.jpg)
        String imagemUrl = fileStorageService.salvarImagemAnuncio(imagem);

        // 2. Monta o DTO normalmente, só que com a URL gerada (não a que o usuário digitou)
        AnuncioDTO dto = new AnuncioDTO();
        dto.setTitulo(titulo);
        dto.setDescricao(descricao);
        dto.setImagemUrl(imagemUrl);
        dto.setLinkDestino(linkDestino);
        dto.setPosicao(posicao);
        dto.setDataInicio(dataInicio);
        dto.setDataFim(dataFim);
        dto.setAtivo(ativo);

        return ResponseEntity.ok(anuncioService.criar(dto));
    }

    @PatchMapping("/admin/anuncios/{id}/toggle")
    public ResponseEntity<Void> toggle(@PathVariable Long id) {
        anuncioService.toggleAtivo(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/anuncios/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        anuncioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}