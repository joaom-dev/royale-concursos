package com.royaleconcursos.repository;

import com.royaleconcursos.model.Concurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConcursoRepository extends JpaRepository<Concurso, Long> {

    // Todos de um estado
    List<Concurso> findByUfIgnoreCase(String uf);

    // Por tipo: "aberto" ou "previsto"
    List<Concurso> findByTipo(String tipo);

    // Por estado + tipo combinados
    List<Concurso> findByUfIgnoreCaseAndTipo(String uf, String tipo);

    // Busca por texto no nome do órgão (para a barra de pesquisa)
    List<Concurso> findByOrgaoContainingIgnoreCase(String orgao);

    // Busca por texto no órgão + filtra por tipo
    List<Concurso> findByOrgaoContainingIgnoreCaseAndTipo(String orgao, String tipo);

    // Busca por texto no órgão + filtra por UF
    List<Concurso> findByOrgaoContainingIgnoreCaseAndUfIgnoreCase(String orgao, String uf);

    // Usado pelo scheduler para limpar os dados antigos de um estado antes de re-importar
    void deleteByUfIgnoreCase(String uf);
}