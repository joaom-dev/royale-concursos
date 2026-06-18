package com.royaleconcursos.service;

import com.royaleconcursos.enums.Plano;
import com.royaleconcursos.model.User;
import com.royaleconcursos.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanoService {

    private final UserRepository userRepository;

    @Transactional
    public void ativarPlanoMensal(String usuarioId) {
        User usuario = buscarUsuario(usuarioId);
        usuario.setPlano(Plano.MENSAL);
        usuario.setPlanoExpiraEm(LocalDateTime.now().plusDays(30));
        userRepository.save(usuario);
        log.info("Plano MENSAL ativado para usuário {} — expira em {}", usuarioId, usuario.getPlanoExpiraEm());
    }

    @Transactional
    public void ativarPlanoVitalicio(String usuarioId) {
        User usuario = buscarUsuario(usuarioId);
        usuario.setPlano(Plano.VITALICIO);
        usuario.setPlanoExpiraEm(null); // nunca expira
        userRepository.save(usuario);
        log.info("Plano VITALICIO ativado para usuário {}", usuarioId);
    }

    @Transactional
    public void rebaixarParaFree(String usuarioId) {
        User usuario = buscarUsuario(usuarioId);
        usuario.setPlano(Plano.FREE);
        usuario.setPlanoExpiraEm(null);
        userRepository.save(usuario);
        log.info("Usuário {} rebaixado para plano FREE", usuarioId);
    }


    public Map<String, Object> getFuncionalidades() {
        User usuario = getUsuarioAutenticado();
        Plano planoAtual = getPlanoEfetivo(usuario);

        Map<String, Object> funcionalidades = new HashMap<>();
        funcionalidades.put("plano",           planoAtual.name());
        funcionalidades.put("exibirAnuncios",  !planoAtual.isSemAnuncios());
        funcionalidades.put("rankingCompleto", planoAtual.isRankingCompleto());
        funcionalidades.put("limiteRanking",   planoAtual.getLimiteRanking() == Integer.MAX_VALUE
                                                ? null // null = sem limite
                                                : planoAtual.getLimiteRanking());
        funcionalidades.put("premium",         planoAtual.isPremium());
        funcionalidades.put("planoExpiraEm",   usuario.getPlanoExpiraEm());

        return funcionalidades;
    }

    public void exigirPremium(String feature) {
        User usuario = getUsuarioAutenticado();
        Plano planoAtual = getPlanoEfetivo(usuario);

        if (!planoAtual.isPremium()) {
            throw new SecurityException("A funcionalidade '" + feature
                    + "' requer plano MENSAL ou VITALICIO. Plano atual: " + planoAtual);
        }
    }

    public void exigirRankingCompleto() {
        User usuario = getUsuarioAutenticado();
        Plano planoAtual = getPlanoEfetivo(usuario);

        if (!planoAtual.isRankingCompleto()) {
            throw new SecurityException("O ranking completo requer plano VITALICIO. Plano atual: " + planoAtual);
        }
    }


    /**
     * @Scheduled
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expirarPlanosMensaisVencidos() {
        log.info("Verificando planos mensais expirados...");

        userRepository.findAll().stream()
            .filter(u -> u.getPlano() == Plano.MENSAL)
            .filter(u -> u.getPlanoExpiraEm() != null)
            .filter(u -> LocalDateTime.now().isAfter(u.getPlanoExpiraEm()))
            .forEach(u -> {
                u.setPlano(Plano.FREE);
                u.setPlanoExpiraEm(null);
                userRepository.save(u);
                log.info("Plano MENSAL expirado para usuário {}", u.getId());
            });
    }

    public Plano getPlanoEfetivo(User usuario) {
        if (usuario.getPlano() == Plano.MENSAL && !usuario.isPlanoAtivo()) {
            return Plano.FREE; 
        }
        return usuario.getPlano();
    }

    private User buscarUsuario(String id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }

    private User getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));
    }
}
