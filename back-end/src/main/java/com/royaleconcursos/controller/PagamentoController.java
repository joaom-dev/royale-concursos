package com.royaleconcursos.controller;

import com.royaleconcursos.dto.PagamentoDTO.*;
import com.royaleconcursos.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @GetMapping("/precos")
    public ResponseEntity<Map<String, BigDecimal>> getPrecos() {
        return ResponseEntity.ok(pagamentoService.getPrecos());
    }

    @PostMapping
    public ResponseEntity<?> criarPagamento(@RequestBody CriarPagamentoRequest request) {
        try {
            return ResponseEntity.ok(pagamentoService.criarPagamento(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * POST /api/pagamentos/{id}/confirmar-pix
     * Usuário clicou em "Já realizei o pagamento" — aprova o PIX e ativa o plano.
     */
    @PostMapping("/{id}/confirmar-pix")
    public ResponseEntity<?> confirmarPix(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pagamentoService.confirmarPix(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<PagamentoResponse>> listarMeusPagamentos() {
        return ResponseEntity.ok(pagamentoService.listarMeusPagamentos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PagamentoResponse> cancelarPagamento(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.cancelarPagamento(id));
    }
}