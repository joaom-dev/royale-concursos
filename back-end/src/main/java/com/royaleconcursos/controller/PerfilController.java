package com.royaleconcursos.controller;

import com.royaleconcursos.dto.PerfilDTO;
import com.royaleconcursos.dto.AtualizarPerfilDTO;
import com.royaleconcursos.service.PerfilService;
import com.royaleconcursos.dto.AlterarSenhaDTO;
import com.royaleconcursos.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/perfil")
public class PerfilController {

    private final PerfilService perfilService;
    private final UserService userService;

    PerfilController(UserService userService, PerfilService perfilService) {
        this.userService = userService;
        this.perfilService = perfilService;
    }

    @GetMapping
    public ResponseEntity<PerfilDTO> getPerfil(Authentication auth) {
        return ResponseEntity.ok(perfilService.buscarPerfil(auth.getName()));
    }

    @PostMapping("/foto")
    public ResponseEntity<String> uploadFoto(
            @RequestParam("foto") MultipartFile arquivo,
            Authentication auth) throws Exception {
        String url = perfilService.salvarFoto(auth.getName(), arquivo);
        return ResponseEntity.ok(url);
    }

    @PutMapping
    public ResponseEntity<String> atualizarPerfil(
            @RequestBody AtualizarPerfilDTO dto,
            Authentication auth) {
        try {
            perfilService.atualizarPerfil(auth.getName(), dto);
            return ResponseEntity.ok("Perfil atualizado com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/senha")
    public ResponseEntity<String> alterarSenha(
            @RequestBody AlterarSenhaDTO dto,
            Authentication auth) {
        try {
            userService.alterarSenha(auth.getName(), dto);
            return ResponseEntity.ok("Senha alterada com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping
    public ResponseEntity<String> excluirConta(Authentication auth) {
        try {
            perfilService.excluirConta(auth.getName());
            return ResponseEntity.ok("Conta excluída com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}