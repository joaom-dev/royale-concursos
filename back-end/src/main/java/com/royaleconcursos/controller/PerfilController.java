package com.royaleconcursos.controller;

import com.royaleconcursos.dto.PerfilDTO;
import com.royaleconcursos.dto.AtualizarPerfilDTO;
import com.royaleconcursos.service.PerfilService;
import com.royaleconcursos.dto.AlterarSenhaDTO;
import com.royaleconcursos.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @Autowired
    private UserService userService;

    // Busca os dados do perfil do usuário autenticado
    @GetMapping
    public ResponseEntity<PerfilDTO> getPerfil(Authentication auth) {
        String email = auth.getName();
        return ResponseEntity.ok(perfilService.buscarPerfil(email));
    }

    // Upload de foto de perfil
    @PostMapping("/foto")
    public ResponseEntity<String> uploadFoto(
            @RequestParam("foto") MultipartFile arquivo,
            Authentication auth) throws Exception {
        String url = perfilService.salvarFoto(auth.getName(), arquivo);
        return ResponseEntity.ok(url);
    }

    // Atualiza nome e/ou email do usuário
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

    // Altera a senha do usuário
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
}