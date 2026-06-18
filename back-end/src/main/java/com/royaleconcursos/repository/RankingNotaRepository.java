package com.royaleconcursos.repository;

import com.royaleconcursos.model.RankingNota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RankingNotaRepository extends JpaRepository<RankingNota, Long> {

    // Ranking de um concurso ordenado por nota
    List<RankingNota> findByConcursoIdOrderByNotaDesc(Long concursoId);

    // Verifica se esse CPF já tem nota nesse concurso (bloqueia duplicata)
    Optional<RankingNota> findByConcursoIdAndCpf(Long concursoId, String cpf);

    // Conta em quantos concursos distintos esse CPF já participou (limite do plano FREE)
    long countByCpf(String cpf);

    // Pesquisa por nome dentro de um concurso
    List<RankingNota> findByConcursoIdAndNomeContainingIgnoreCaseOrderByNotaDesc(
        Long concursoId, String nome
    );
}