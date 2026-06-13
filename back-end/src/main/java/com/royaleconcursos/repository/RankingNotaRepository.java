package com.royaleconcursos.repository;

import com.royaleconcursos.model.RankingNota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingNotaRepository extends JpaRepository<RankingNota, Long> {

    // Ranking de um concurso ordenado por nota (maior primeiro)
    List<RankingNota> findByConcursoIdOrderByNotaDesc(Long concursoId);

    // Verificar duplicata: mesmo nome no mesmo concurso
    Optional<RankingNota> findByConcursoIdAndNomeIgnoreCase(Long concursoId, String nome);
}