package com.royaleconcursos.service;

import com.royaleconcursos.dto.RankingNotaDTO;
import com.royaleconcursos.enums.Plano;
import com.royaleconcursos.model.RankingNota;
import com.royaleconcursos.model.User;
import com.royaleconcursos.repository.RankingNotaRepository;
import com.royaleconcursos.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingNotaService {

    private final RankingNotaRepository repository;
    private final UserRepository userRepository;

    public RankingNotaService(RankingNotaRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public RankingNotaDTO salvar(String email, RankingNotaDTO dto) {

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Plano FREE: só 1 participação no total, identificado pelo CPF do User
        boolean isFree = user.getPlano() == null || user.getPlano() == Plano.FREE;
        if (isFree) {
            long totalUsos = repository.countParticipacoesByCpf(user.getCpf());
            if (totalUsos >= 1) {
                throw new IllegalStateException(
                    "Seu plano FREE permite apenas 1 participação no ranking. Assine para participar ilimitadamente!"
                );
            }
        }

        // Bloqueia mesmo usuário no mesmo concurso
        if (repository.existsByConcursoIdAndNomeIgnoreCase(dto.getConcursoId(), user.getName())) {
            throw new IllegalArgumentException("Você já cadastrou uma nota neste concurso.");
        }

        RankingNota entidade = new RankingNota();
        entidade.setConcursoId(dto.getConcursoId());
        entidade.setNome(user.getName());
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