package com.royaleconcursos.model;

import com.royaleconcursos.enums.MetodoPagamento;
import com.royaleconcursos.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade Pagamento — representa uma transação (PIX, cartão ou carteira digital).
 */
@Entity
@Table(name = "pagamentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPagamento metodoPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusPagamento status = StatusPagamento.PENDENTE;

    private String descricao;

    /**
     * ID externo da transação (gateway de pagamento, PIX txId, etc.)
     */
    private String idTransacaoExterna;

    /**
     * Payload PIX "copia e cola" (apenas quando metodoPagamento == PIX).
     */
    @Column(columnDefinition = "TEXT")
    private String pixPayload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private User usuario;

    @Column(updatable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
