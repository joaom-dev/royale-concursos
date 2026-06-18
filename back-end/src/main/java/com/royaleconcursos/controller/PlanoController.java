package com.royaleconcursos.controller;

import com.royaleconcursos.service.PlanoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/planos")
@RequiredArgsConstructor
public class PlanoController {

    private final PlanoService planoService;

    @GetMapping("/funcionalidades")
    public ResponseEntity<Map<String, Object>> getFuncionalidades() {
        return ResponseEntity.ok(planoService.getFuncionalidades());
    }

    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> getRanking() {
        Map<String, Object> funcionalidades = planoService.getFuncionalidades();
        boolean completo = (boolean) funcionalidades.get("rankingCompleto");
        Integer limite   = (Integer) funcionalidades.get("limiteRanking");

        return ResponseEntity.ok(Map.of(
            "rankingCompleto", completo,
            "limiteExibido",   limite != null ? limite : "sem limite",
            "mensagem",        completo
                ? "Ranking completo disponível — plano VITALICIO"
                : "Exibindo apenas top " + limite + ". Faça upgrade para ver o ranking completo."
        ));
    }

    @GetMapping("/verificar-premium")
    public ResponseEntity<Map<String, Object>> verificarPremium() {
        planoService.exigirPremium("acesso premium");
        return ResponseEntity.ok(Map.of("acesso", true, "mensagem", "Plano premium ativo"));
    }
}