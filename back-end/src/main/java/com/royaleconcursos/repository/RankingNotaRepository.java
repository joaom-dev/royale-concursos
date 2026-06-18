package com.royaleconcursos.repository;

import com.royaleconcursos.model.RankingNota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingNotaRepository extends JpaRepository<RankingNota, Long> {

    List<RankingNota> findByConcursoIdOrderByNotaDesc(Long concursoId);

    Optional<RankingNota> findByConcursoIdAndCpf(Long concursoId, String cpf);

    long countByCpf(String cpf);

    List<RankingNota> findByConcursoIdAndNomeContainingIgnoreCaseOrderByNotaDesc(
        Long concursoId, String nome
    );
}