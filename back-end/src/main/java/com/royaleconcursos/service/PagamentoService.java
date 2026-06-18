package com.royaleconcursos.service;

import com.royaleconcursos.dto.PagamentoDTO.*;
import com.royaleconcursos.enums.MetodoPagamento;
import com.royaleconcursos.enums.StatusPagamento;
import com.royaleconcursos.model.Pagamento;
import com.royaleconcursos.model.User;
import com.royaleconcursos.repository.PagamentoRepository;
import com.royaleconcursos.repository.UserRepository;
import com.royaleconcursos.service.pix.PixService;
import com.royaleconcursos.validator.CartaoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final UserRepository userRepository;
    private final CartaoValidator cartaoValidator;
    private final PixService pixService;
    private final PlanoService planoService;

    // ── Preços definidos SOMENTE no backend ───────────────────────────────────
    private static final Map<String, BigDecimal> PRECOS_PLANO = Map.of(
        "MENSAL",    new BigDecimal("17.90"),
        "VITALICIO", new BigDecimal("64.90")
    );

    /**
     * Retorna os preços dos planos para exibição no frontend (somente leitura).
     * O frontend exibe, mas NÃO envia de volta — o backend sempre usa PRECOS_PLANO.
     */
    public Map<String, BigDecimal> getPrecos() {
        return PRECOS_PLANO;
    }

    @Transactional
    public PagamentoResponse criarPagamento(CriarPagamentoRequest request) {
        User usuarioAtual = getUsuarioAutenticado();

        // Valida que o tipoPlano foi informado
        if (request.getTipoPlano() == null || request.getTipoPlano().isBlank()) {
            throw new IllegalArgumentException("Tipo de plano é obrigatório.");
        }

        // ── Valor definido pelo BACKEND, ignorando qualquer valor enviado pelo frontend ──
        BigDecimal valorReal = PRECOS_PLANO.get(request.getTipoPlano().toUpperCase());
        if (valorReal == null) {
            throw new IllegalArgumentException("Plano inválido: " + request.getTipoPlano());
        }

        // Valida cartão ANTES de criar registro
        cartaoValidator.validar(
            request.getNumeroCartao(),
            request.getValidadeCartao(),
            request.getCvv(),
            request.getMetodoPagamento()
        );

        // Cria o pagamento com o valor REAL do backend
        Pagamento pagamento = Pagamento.builder()
                .valor(valorReal)
                .metodoPagamento(request.getMetodoPagamento())
                .descricao("Plano " + request.getTipoPlano() + " - Royale Concursos")
                .usuario(usuarioAtual)
                .build();
        pagamento = pagamentoRepository.save(pagamento);
        log.info("Pagamento criado: id={}, valor={}, plano={}", pagamento.getId(), valorReal, request.getTipoPlano());

        pagamento = processarPagamento(pagamento, request, usuarioAtual);
        pagamento = pagamentoRepository.save(pagamento);

        if (pagamento.getStatus() == StatusPagamento.APROVADO) {
            ativarPlanoAposCompra(usuarioAtual.getId(), request.getTipoPlano());
        }

        return toResponse(pagamento);
    }

    private Pagamento processarPagamento(Pagamento pagamento, CriarPagamentoRequest request, User usuario) {
        pagamento.setStatus(StatusPagamento.PROCESSANDO);
        String txId = "PAY" + pagamento.getId();

        if (pagamento.getMetodoPagamento() == MetodoPagamento.PIX) {
            String payload = pixService.gerarPayload(
                "olivercmd250@gmail.com",
                "Joao Marcos Marques Silva",
                "Novo Gama",
                pagamento.getValor(),
                txId
            );
            pagamento.setIdTransacaoExterna("PIX-" + txId);
            pagamento.setPixPayload(payload);
            pagamento.setStatus(StatusPagamento.PENDENTE);

        } else if (pagamento.getMetodoPagamento() == MetodoPagamento.CARTAO_CREDITO
                || pagamento.getMetodoPagamento() == MetodoPagamento.CARTAO_DEBITO) {
            pagamento.setIdTransacaoExterna("CARD-" + UUID.randomUUID());
            pagamento.setStatus(StatusPagamento.APROVADO);

        } else if (pagamento.getMetodoPagamento() == MetodoPagamento.CARTEIRA_DIGITAL) {
            if (request.getIdCarteiraDigital() == null) {
                pagamento.setStatus(StatusPagamento.RECUSADO);
            } else {
                pagamento.setIdTransacaoExterna("WALLET-" + UUID.randomUUID());
                pagamento.setStatus(StatusPagamento.APROVADO);
            }
        }

        return pagamento;
    }

    private void ativarPlanoAposCompra(String usuarioId, String tipoPlano) {
        switch (tipoPlano.toUpperCase()) {
            case "MENSAL"    -> planoService.ativarPlanoMensal(usuarioId);
            case "VITALICIO" -> planoService.ativarPlanoVitalicio(usuarioId);
            default -> log.warn("Tipo de plano desconhecido: {}", tipoPlano);
        }
    }

    public List<PagamentoResponse> listarMeusPagamentos() {
        User usuarioAtual = getUsuarioAutenticado();
        return pagamentoRepository.findByUsuarioId(usuarioAtual.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PagamentoResponse buscarPorId(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado: " + id));
        User usuarioAtual = getUsuarioAutenticado();
        if (!pagamento.getUsuario().getId().equals(usuarioAtual.getId())) {
            throw new SecurityException("Acesso negado ao pagamento " + id);
        }
        return toResponse(pagamento);
    }

    @Transactional
    public PagamentoResponse cancelarPagamento(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado: " + id));
        if (pagamento.getStatus() == StatusPagamento.APROVADO) {
            throw new IllegalStateException("Pagamento já aprovado não pode ser cancelado.");
        }
        pagamento.setStatus(StatusPagamento.CANCELADO);
        return toResponse(pagamentoRepository.save(pagamento));
    }

    @Transactional
    public PagamentoResponse confirmarPix(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado: " + id));

        User usuarioAtual = getUsuarioAutenticado();
        if (!pagamento.getUsuario().getId().equals(usuarioAtual.getId())) {
            throw new SecurityException("Acesso negado ao pagamento " + id);
        }

        if (pagamento.getStatus() != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Apenas pagamentos pendentes podem ser confirmados.");
        }

        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamento = pagamentoRepository.save(pagamento);

        // Descobre o plano pela descrição do pagamento
        String desc = pagamento.getDescricao() != null ? pagamento.getDescricao().toUpperCase() : "";
        if (desc.contains("VITALICIO") || desc.contains("VITALÍCIO")) {
            planoService.ativarPlanoVitalicio(usuarioAtual.getId());
        } else if (desc.contains("MENSAL")) {
            planoService.ativarPlanoMensal(usuarioAtual.getId());
        }

        log.info("PIX confirmado: pagamento id={}, usuário={}", id, usuarioAtual.getEmail());
        return toResponse(pagamento);
    }

    private User getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private PagamentoResponse toResponse(Pagamento p) {
        PagamentoResponse response = new PagamentoResponse();
        response.setId(p.getId());
        response.setValor(p.getValor());
        response.setMetodoPagamento(p.getMetodoPagamento());
        response.setStatus(p.getStatus());
        response.setDescricao(p.getDescricao());
        response.setIdTransacaoExterna(p.getIdTransacaoExterna());
        response.setPixPayload(p.getPixPayload());
        response.setCriadoEm(p.getCriadoEm());
        response.setAtualizadoEm(p.getAtualizadoEm());
        response.setNomeUsuario(p.getUsuario().getName());
        return response;
    }
}