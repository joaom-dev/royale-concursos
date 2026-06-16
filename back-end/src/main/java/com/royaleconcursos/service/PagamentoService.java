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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Serviço de pagamentos atualizado:
 *   - Valida dados de cartão via CartaoValidator (algoritmo de Luhn)
 *   - Gera payload PIX via PixService
 *   - Ativa planos via PlanoService após pagamento aprovado
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final UserRepository userRepository;
    private final CartaoValidator cartaoValidator;
    private final PixService pixService;
    private final PlanoService planoService;

    /**
     * Cria e processa um pagamento.
     *
     * Fluxo:
     *   1. Valida os dados do cartão (se for cartão)
     *   2. Cria o registro no banco com status PENDENTE
     *   3. Processa pelo método de pagamento
     *   4. Se aprovado e for pagamento de PLANO, ativa o plano
     */
    @Transactional
    public PagamentoResponse criarPagamento(CriarPagamentoRequest request) {
        User usuarioAtual = getUsuarioAutenticado();

        // 1. Valida cartão ANTES de criar qualquer registro no banco
        // Lança IllegalArgumentException se algum dado for inválido
        cartaoValidator.validar(
            request.getNumeroCartao(),
            request.getValidadeCartao(),
            request.getCvv(),
            request.getMetodoPagamento()
        );

        // 2. Cria o pagamento no banco
        Pagamento pagamento = Pagamento.builder()
                .valor(request.getValor())
                .metodoPagamento(request.getMetodoPagamento())
                .descricao(request.getDescricao())
                .usuario(usuarioAtual)
                .build();
        pagamento = pagamentoRepository.save(pagamento);
        log.info("Pagamento criado: id={}, valor={}, método={}", pagamento.getId(), pagamento.getValor(), pagamento.getMetodoPagamento());

        // 3. Processa e atualiza o status
        pagamento = processarPagamento(pagamento, request, usuarioAtual);
        pagamento = pagamentoRepository.save(pagamento);

        // 4. Se aprovado e for pagamento de plano, ativa o plano
        if (pagamento.getStatus() == StatusPagamento.APROVADO && request.getTipoPlano() != null) {
            ativarPlanoAposCompra(usuarioAtual.getId(), request.getTipoPlano());
        }

        return toResponse(pagamento);
    }

    /**
     * Processa o pagamento de acordo com o método escolhido.
     *
     * Para PIX: gera o payload e armazena no campo pixPayload da resposta.
     * Para cartão: o CartaoValidator já validou, aqui aprovamos (em prod: chamaria gateway).
     * Para carteira digital: verifica se o ID foi informado.
     */
    private Pagamento processarPagamento(Pagamento pagamento, CriarPagamentoRequest request, User usuario) {
        pagamento.setStatus(StatusPagamento.PROCESSANDO);
        String txId = "PAY" + pagamento.getId();

        if (pagamento.getMetodoPagamento() == MetodoPagamento.PIX) {
            log.info("Gerando payload PIX para pagamento id={}", pagamento.getId());

            // Gera o payload PIX real (padrão BACEN)
            String payload = pixService.gerarPayload(
                "sua-chave-pix@email.com", // substitua pela chave PIX da sua conta
                "Minha Loja",
                "Sao Paulo",
                pagamento.getValor(),
                txId
            );

            pagamento.setIdTransacaoExterna("PIX-" + txId);
            pagamento.setPixPayload(payload);        // payload "copia e cola"
            pagamento.setStatus(StatusPagamento.PENDENTE); // PIX fica PENDENTE até confirmação do BACEN

        } else if (pagamento.getMetodoPagamento() == MetodoPagamento.CARTAO_CREDITO
                || pagamento.getMetodoPagamento() == MetodoPagamento.CARTAO_DEBITO) {
            // Cartão já foi validado pelo CartaoValidator antes de chegar aqui
            // Em produção: chame o SDK do gateway (Mercado Pago, Stripe, etc.)
            log.info("Processando cartão para pagamento id={}", pagamento.getId());
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

    /**
     * Ativa o plano correto após o pagamento ser aprovado.
     */
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
