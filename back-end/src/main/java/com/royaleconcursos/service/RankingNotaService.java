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

        String cpf = user.getCpf();

        // 1. Bloqueia duplicata: mesmo CPF no mesmo concurso
        boolean jaExisteNesseConcurso = repository
            .findByConcursoIdAndCpf(dto.getConcursoId(), cpf)
            .isPresent();

        if (jaExisteNesseConcurso) {
            throw new IllegalArgumentException("Você já cadastrou uma nota neste concurso.");
        }

        // 2. Plano FREE: só pode ter nota em 1 concurso no total
        boolean isFree = user.getPlano() == null || user.getPlano() == Plano.FREE;
        if (isFree) {
            long totalConcursos = repository.countByCpf(cpf);
            if (totalConcursos >= 1) {
                throw new IllegalStateException(
                    "Seu plano FREE permite apenas 1 participação no ranking. Assine para participar ilimitadamente!"
                );
            }
        }

        // 3. Salva
        RankingNota entidade = new RankingNota();
        entidade.setConcursoId(dto.getConcursoId());
        entidade.setNome(user.getName());
        entidade.setCpf(cpf);
        entidade.setUserId(user.getId());
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

    public List<RankingNotaDTO> pesquisar(Long concursoId, String nome) {
        return repository
            .findByConcursoIdAndNomeContainingIgnoreCaseOrderByNotaDesc(concursoId, nome)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    // Máscara: 123.456.789-00 → ***.456.789-**
    private String mascararCpf(String cpf) {
        if (cpf == null || cpf.length() < 11) return "***.***.***-**";
        String limpo = cpf.replaceAll("[^0-9]", "");
        if (limpo.length() < 9) return "***.***.***-**";
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