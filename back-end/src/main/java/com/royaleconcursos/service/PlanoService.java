package com.royaleconcursos.service;

import com.royaleconcursos.enums.Plano;
import com.royaleconcursos.model.Usuario;
import com.royaleconcursos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Serviço que gerencia os planos dos usuários.
 *
 * Responsabilidades:
 *   - Ativar/desativar planos após pagamento confirmado
 *   - Retornar as funcionalidades disponíveis para o plano ativo
 *   - Verificar acesso a features específicas
 *   - Expirar planos mensais automaticamente (agendado)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PlanoService {

    private final UsuarioRepository usuarioRepository;

    // ── Ativação de planos ─────────────────────────────────────────────────

    /**
     * Ativa o plano MENSAL para um usuário.
     * Chamado pelo PagamentoService quando um pagamento mensal é aprovado.
     *
     * O plano dura 30 dias a partir da ativação.
     * Se já for MENSAL, renova por mais 30 dias a partir de HOJE (não acumula).
     */
    @Transactional
    public void ativarPlanoMensal(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        usuario.setPlano(Plano.MENSAL);
        usuario.setPlanoExpiraEm(LocalDateTime.now().plusDays(30));
        usuarioRepository.save(usuario);
        log.info("Plano MENSAL ativado para usuário {} — expira em {}", usuarioId, usuario.getPlanoExpiraEm());
    }

    /**
     * Ativa o plano VITALICIO para um usuário.
     * Chamado pelo PagamentoService quando um pagamento vitalício é aprovado.
     *
     * Não tem data de expiração — planoExpiraEm é definido como null.
     */
    @Transactional
    public void ativarPlanoVitalicio(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        usuario.setPlano(Plano.VITALICIO);
        usuario.setPlanoExpiraEm(null); // nunca expira
        usuarioRepository.save(usuario);
        log.info("Plano VITALICIO ativado para usuário {}", usuarioId);
    }

    /**
     * Rebaixa o usuário para o plano FREE.
     * Chamado quando o plano mensal expira ou é cancelado.
     */
    @Transactional
    public void rebaixarParaFree(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        usuario.setPlano(Plano.FREE);
        usuario.setPlanoExpiraEm(null);
        usuarioRepository.save(usuario);
        log.info("Usuário {} rebaixado para plano FREE", usuarioId);
    }

    // ── Consulta de funcionalidades ────────────────────────────────────────

    /**
     * Retorna um mapa de funcionalidades disponíveis para o usuário autenticado.
     *
     * O front-end usa este endpoint para decidir o que mostrar:
     *   - exibirAnuncios: true → mostra banners/ads
     *   - rankingCompleto: true → mostra a tabela completa
     *   - limiteRanking: quantas posições mostrar
     *   - plano: nome do plano atual
     *
     * Exemplo de resposta para FREE:
     * {
     *   "plano": "FREE",
     *   "exibirAnuncios": true,
     *   "rankingCompleto": false,
     *   "limiteRanking": 10,
     *   "premium": false
     * }
     */
    public Map<String, Object> getFuncionalidades() {
        Usuario usuario = getUsuarioAutenticado();
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

    /**
     * Verifica se o usuário autenticado tem acesso a uma feature premium.
     * Lança exceção se não tiver — o controller retorna 403 automaticamente.
     */
    public void exigirPremium(String feature) {
        Usuario usuario = getUsuarioAutenticado();
        Plano planoAtual = getPlanoEfetivo(usuario);

        if (!planoAtual.isPremium()) {
            throw new SecurityException("A funcionalidade '" + feature
                    + "' requer plano MENSAL ou VITALICIO. Plano atual: " + planoAtual);
        }
    }

    /**
     * Verifica especificamente se o usuário tem acesso ao ranking completo.
     * Apenas VITALICIO tem esse acesso.
     */
    public void exigirRankingCompleto() {
        Usuario usuario = getUsuarioAutenticado();
        Plano planoAtual = getPlanoEfetivo(usuario);

        if (!planoAtual.isRankingCompleto()) {
            throw new SecurityException("O ranking completo requer plano VITALICIO. Plano atual: " + planoAtual);
        }
    }

    // ── Tarefa agendada: expirar planos mensais ────────────────────────────

    /**
     * Executa todo dia à meia-noite (cron "0 0 0 * * *").
     * Busca todos os usuários com plano MENSAL expirado e rebaixa para FREE.
     *
     * @Scheduled exige @EnableScheduling na classe principal ou em uma @Configuration.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expirarPlanosMensaisVencidos() {
        log.info("Verificando planos mensais expirados...");

        usuarioRepository.findAll().stream()
            .filter(u -> u.getPlano() == Plano.MENSAL)
            .filter(u -> u.getPlanoExpiraEm() != null)
            .filter(u -> LocalDateTime.now().isAfter(u.getPlanoExpiraEm()))
            .forEach(u -> {
                u.setPlano(Plano.FREE);
                u.setPlanoExpiraEm(null);
                usuarioRepository.save(u);
                log.info("Plano MENSAL expirado para usuário {}", u.getId());
            });
    }

    // ── Utilitários ────────────────────────────────────────────────────────

    /**
     * Retorna o plano EFETIVO do usuário.
     *
     * Por que isso existe? Um usuário pode estar cadastrado como MENSAL
     * mas o plano pode ter expirado. Nesse caso, o plano efetivo é FREE,
     * mesmo que o banco ainda mostre MENSAL (até a tarefa agendada rodar).
     *
     * Isso garante que o sistema nunca dê acesso indevido por atraso
     * na execução da tarefa agendada.
     */
    public Plano getPlanoEfetivo(Usuario usuario) {
        if (usuario.getPlano() == Plano.MENSAL && !usuario.isPlanoAtivo()) {
            return Plano.FREE; // expirou, trata como FREE imediatamente
        }
        return usuario.getPlano();
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado"));
    }
}
