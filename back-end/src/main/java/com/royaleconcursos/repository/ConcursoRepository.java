package com.royaleconcursos.repository;

import com.royaleconcursos.model.Concurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcursoRepository extends JpaRepository<Concurso, Long> {

    List<Concurso> findByUfIgnoreCase(String uf);

    List<Concurso> findByTipo(String tipo);

    List<Concurso> findByUfIgnoreCaseAndTipo(String uf, String tipo);

    List<Concurso> findByOrgaoContainingIgnoreCase(String orgao);

    List<Concurso> findByOrgaoContainingIgnoreCaseAndTipo(String orgao, String tipo);

    List<Concurso> findByOrgaoContainingIgnoreCaseAndUfIgnoreCase(String orgao, String uf);

    void deleteByUfIgnoreCase(String uf);
}