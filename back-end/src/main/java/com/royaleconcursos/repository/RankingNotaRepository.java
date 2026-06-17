package com.royaleconcursos.repository;

import com.royaleconcursos.model.RankingNota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankingNotaRepository extends JpaRepository<RankingNota, Long> {

    List<RankingNota> findByConcursoIdOrderByNotaDesc(Long concursoId);

    boolean existsByConcursoIdAndNomeIgnoreCase(Long concursoId, String nome);

    // Conta participações do usuário pelo CPF via JOIN com a tabela users
    @Query("SELECT COUNT(r) FROM RankingNota r JOIN User u ON r.nome = u.name WHERE u.cpf = :cpf")
    long countParticipacoesByCpf(@Param("cpf") String cpf);
}