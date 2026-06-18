package com.royaleconcursos.dto;

import com.royaleconcursos.enums.MetodoPagamento;
import com.royaleconcursos.enums.StatusPagamento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriarPagamentoRequest {

        private BigDecimal valor;

        private MetodoPagamento metodoPagamento;

        private String descricao;

        private String tipoPlano;

        private String numeroCartao;
        private String validadeCartao; 
        private String cvv;
        private String nomeTitular;

        private String idCarteiraDigital;
    }

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
