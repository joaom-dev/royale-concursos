package com.royaleconcursos.repository;

import com.royaleconcursos.model.Anuncio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {

    @Query("SELECT a FROM Anuncio a WHERE a.ativo = true " +
    "AND (a.dataInicio IS NULL OR a.dataInicio <= :agora) " +
    "AND (a.dataFim IS NULL OR a.dataFim >= :agora)")
    List<Anuncio> findAnunciosAtivos(LocalDateTime agora);

    List<Anuncio> findByPosicaoAndAtivoTrue(String posicao);
}