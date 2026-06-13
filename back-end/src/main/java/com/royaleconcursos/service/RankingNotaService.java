package com.royaleconcursos.service;

import com.royaleconcursos.dto.RankingNotaDTO;
import com.royaleconcursos.model.RankingNota;
import com.royaleconcursos.repository.RankingNotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingNotaService {

    private final RankingNotaRepository repository;

    public RankingNotaService(RankingNotaRepository repository) {
        this.repository = repository;
    }

    public RankingNotaDTO salvar(RankingNotaDTO dto) {

        // Bloqueia mesmo nome no mesmo concurso
        boolean jaExiste = repository
                .findByConcursoIdAndNomeIgnoreCase(dto.getConcursoId(), dto.getNome())
                .isPresent();

        if (jaExiste) {
            throw new IllegalArgumentException(
                "\"" + dto.getNome() + "\" já está cadastrado neste concurso."
            );
        }

        RankingNota entidade = new RankingNota();
        entidade.setConcursoId(dto.getConcursoId());
        entidade.setNome(dto.getNome());
        entidade.setNota(dto.getNota());
        entidade.setFotoUrl(dto.getFotoUrl());

        return toDTO(repository.save(entidade));
    }

    public List<RankingNotaDTO> listarPorConcurso(Long concursoId) {
        return repository
                .findByConcursoIdOrderByNotaDesc(concursoId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private RankingNotaDTO toDTO(RankingNota r) {
        return new RankingNotaDTO(
            r.getId(),
            r.getConcursoId(),
            r.getNome(),
            r.getNota(),
            r.getFotoUrl(),
            r.getCriadoEm()
        );
    }
}