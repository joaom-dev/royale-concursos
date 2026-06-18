package com.royaleconcursos.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Plano {

    FREE(
        false,
        false,
        10     
    ),

    MENSAL(
        true, 
        false,
        10     
    ),

    VITALICIO(
        true, 
        true, 
        Integer.MAX_VALUE  
    );

    private final boolean semAnuncios;
    private final boolean rankingCompleto;

    private final int limiteRanking;
    public boolean isPremium() {
        return this == MENSAL || this == VITALICIO;
    }
}