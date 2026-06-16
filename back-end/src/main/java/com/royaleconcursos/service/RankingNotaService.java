package com.royaleconcursos.service;

import com.royaleconcursos.dto.RankingNotaDTO;
import com.royaleconcursos.enums.Plano;
import com.royaleconcursos.model.RankingNota;
import com.royaleconcursos.model.User;
import com.royaleconcursos.repository.RankingNotaRepository;
import com.royaleconcursos.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingNotaService {

    private final RankingNotaRepository repository;
    private final UserRepository userRepository;
    private final PlanoService planoService;

    public RankingNotaService(RankingNotaRepository repository,
                              UserRepository userRepository,
                              PlanoService planoService) {
        this.repository    = repository;
        this.userRepository = userRepository;
        this.planoService  = planoService;
    }

    public RankingNotaDTO salvar(RankingNotaDTO dto) {

        String email = SecurityContextHolder.getContext()
                           .getAuthentication().getName();
        User usuario = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String cpf = usuario.getCpf();

        boolean jaExisteNesseConcurso = repository
                .findByConcursoIdAndCpf(dto.getConcursoId(), cpf)
                .isPresent();

        if (jaExisteNesseConcurso) {
            throw new IllegalArgumentException(
                "Você já possui uma nota cadastrada neste concurso."
            );
        }

        Plano plano = planoService.getPlanoEfetivo(usuario);
        boolean isPremium = plano == Plano.MENSAL || plano == Plano.VITALICIO;

        if (!isPremium) {
            long totalConcursos = repository.countConcursosDistintosByCpf(cpf);
            if (totalConcursos >= 1) {
                throw new SecurityException(
                    "Plano FREE permite nota em apenas 1 concurso. " +
                    "Faça upgrade para adicionar em mais concursos."
                );
            }
        }

        RankingNota entidade = new RankingNota();
        entidade.setConcursoId(dto.getConcursoId());
        entidade.setNome(usuario.getName());
        entidade.setCpf(cpf);
        entidade.setUserId(usuario.getId());
        entidade.setNota(dto.getNota());
        entidade.setFotoUrl(dto.getFotoUrl());

        return toDTO(repository.save(entidade));
    }

    public List<RankingNotaDTO> listarPorConcurso(Long concursoId) {
        return repository.findByConcursoIdOrderByNotaDesc(concursoId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<RankingNotaDTO> pesquisar(Long concursoId, String nome) {
        return repository
            .findByConcursoIdAndNomeContainingIgnoreCaseOrderByNotaDesc(concursoId, nome)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private String mascararCpf(String cpf) {
        if (cpf == null || cpf.length() < 11) return "***.***.***-**";
        String limpo = cpf.replaceAll("[^0-9]", "");
        return "***." + limpo.substring(3, 6) + "." + limpo.substring(6, 9) + "-**";
    }

    private RankingNotaDTO toDTO(RankingNota r) {
        return new RankingNotaDTO(
            r.getId(),
            r.getConcursoId(),
            r.getNome(),
            mascararCpf(r.getCpf()),
            r.getUserId(),
            r.getNota(),
            r.getFotoUrl(),
            r.getCriadoEm()
        );
    }
}