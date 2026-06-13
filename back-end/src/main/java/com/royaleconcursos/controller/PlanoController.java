package com.royaleconcursos.controller;

import com.royaleconcursos.service.PlanoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller de planos: expõe endpoints para o front-end consultar
 * as funcionalidades disponíveis e controlar o que mostrar na UI.
 */
@RestController
@RequestMapping("/api/planos")
@RequiredArgsConstructor
public class PlanoController {

    private final PlanoService planoService;

    /**
     * GET /api/planos/funcionalidades
     *
     * Retorna as funcionalidades do plano atual do usuário autenticado.
     *
     * O front-end chama este endpoint no login e usa a resposta para:
     *   - Exibir ou ocultar anúncios
     *   - Limitar ou liberar o ranking
     *   - Mostrar badge do plano (FREE / MENSAL / VITALICIO)
     *
     * Exemplo de resposta para usuário FREE:
     * {
     *   "plano": "FREE",
     *   "exibirAnuncios": true,
     *   "rankingCompleto": false,
     *   "limiteRanking": 10,
     *   "premium": false,
     *   "planoExpiraEm": null
     * }
     *
     * Exemplo para VITALICIO:
     * {
     *   "plano": "VITALICIO",
     *   "exibirAnuncios": false,
     *   "rankingCompleto": true,
     *   "limiteRanking": null,
     *   "premium": true,
     *   "planoExpiraEm": null
     * }
     */
    @GetMapping("/funcionalidades")
    public ResponseEntity<Map<String, Object>> getFuncionalidades() {
        return ResponseEntity.ok(planoService.getFuncionalidades());
    }

    /**
     * GET /api/planos/ranking
     *
     * Retorna o ranking respeitando o limite do plano do usuário.
     *
     * FREE e MENSAL: retorna apenas os primeiros 10
     * VITALICIO: retorna o ranking completo
     *
     * Em uma aplicação real, este endpoint buscaria os dados do banco.
     * Aqui retorna um exemplo de resposta para ilustrar o comportamento.
     */
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

    /**
     * GET /api/planos/verificar-premium
     *
     * Endpoint utilitário: retorna 200 se o usuário tem acesso premium,
     * 403 se não tiver. Útil para o front-end verificar acesso antes de
     * navegar para uma tela premium.
     */
    @GetMapping("/verificar-premium")
    public ResponseEntity<Map<String, Object>> verificarPremium() {
        planoService.exigirPremium("acesso premium");
        return ResponseEntity.ok(Map.of("acesso", true, "mensagem", "Plano premium ativo"));
    }
}