package com.royaleconcursos.dto;

import com.royaleconcursos.enums.MetodoPagamento;
import com.royaleconcursos.enums.StatusPagamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTOs relacionados ao módulo de pagamentos.
 *
 * Classes:
 *   - CriarPagamentoRequest: dados enviados pelo front-end para criar um pagamento
 *   - PagamentoResponse: dados retornados ao front-end após criar/consultar pagamento
 */
public class PagamentoDTO {

    /**
     * Requisição para criação de pagamento.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriarPagamentoRequest {

        private BigDecimal valor;

        private MetodoPagamento metodoPagamento;

        private String descricao;

        /**
         * Tipo de plano sendo comprado: "MENSAL" ou "VITALICIO".
         * Pode ser null se o pagamento não estiver relacionado a um plano.
         */
        private String tipoPlano;

        // ── Campos de cartão (obrigatórios apenas se metodoPagamento for cartão) ──
        private String numeroCartao;
        private String validadeCartao; // formato MM/AA
        private String cvv;
        private String nomeTitular;

        // ── Campo de carteira digital ──
        private String idCarteiraDigital;
    }

    /**
     * Resposta com os dados do pagamento.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagamentoResponse {

        private Long id;
        private BigDecimal valor;
        private MetodoPagamento metodoPagamento;
        private StatusPagamento status;
        private String descricao;
        private String idTransacaoExterna;
        private String pixPayload;
        private String nomeUsuario;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;
    }
}
