package com.royaleconcursos.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Plano {

    /**
     * FREE: acesso básico com anúncios. Ranking limitado (top 10 apenas).
     */
    FREE(
        false,  // semAnuncios
        false,  // rankingCompleto
        10      // limiteRanking (top 10)
    ),

    /**
     * MENSAL: remove anúncios. Ranking ainda limitado.
     * Renova todo mês via pagamento recorrente.
     */
    MENSAL(
        true,   // semAnuncios
        false,  // rankingCompleto
        10      // limiteRanking (top 10)
    ),

    /**
     * VITALICIO: remove anúncios E libera ranking completo (sem limite).
     * Pagamento único, acesso permanente.
     */
    VITALICIO(
        true,   // semAnuncios
        true,   // rankingCompleto
        Integer.MAX_VALUE  // limiteRanking (sem limite)
    );

    /**
     * true = usuário NÃO vê anúncios
     * false = usuário VÊ anúncios
     */
    private final boolean semAnuncios;

    /**
     * true = usuário pode ver o ranking completo
     * false = usuário vê apenas os primeiros `limiteRanking` itens
     */
    private final boolean rankingCompleto;

    /**
     * Quantas posições do ranking este plano pode visualizar.
     * Integer.MAX_VALUE é usado para representar "sem limite".
     */
    private final int limiteRanking;

    // ── Métodos utilitários ──────────────────────────────────────────────

    /** Verifica se o plano tem acesso a qualquer feature premium. */
    public boolean isPremium() {
        return this == MENSAL || this == VITALICIO;
    }
}