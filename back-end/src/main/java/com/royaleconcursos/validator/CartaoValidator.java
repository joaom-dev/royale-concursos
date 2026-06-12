package com.royaleconcursos.validator;

import com.royaleconcursos.enums.MetodoPagamento;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Validador de dados de cartão de crédito/débito.
 *
 * Realiza três validações:
 *   1. Algoritmo de Luhn — verifica se o número do cartão é matematicamente válido
 *   2. Validade — verifica se o cartão não está expirado
 *   3. CVV — verifica o formato do código de segurança
 *
 * IMPORTANTE: Estas validações checam o FORMATO dos dados, não se o cartão
 * realmente existe ou tem saldo. Essa segunda verificação é feita pelo
 * gateway de pagamento (Mercado Pago, Stripe, etc.).
 */
@Component
public class CartaoValidator {

    /**
     * Valida todos os dados do cartão de uma vez.
     * Lança IllegalArgumentException com mensagem clara se algum dado for inválido.
     */
    public void validar(String numeroCartao, String validade, String cvv, MetodoPagamento metodo) {
        if (metodo != MetodoPagamento.CARTAO_CREDITO && metodo != MetodoPagamento.CARTAO_DEBITO) {
            return; // Não é cartão, não precisa validar
        }

        validarNumero(numeroCartao);
        validarValidade(validade);
        validarCvv(cvv);
    }

    // ── Validação do número (Algoritmo de Luhn) ────────────────────────────

    /**
     * O Algoritmo de Luhn é um padrão internacional (ISO/IEC 7812) usado por
     * todas as bandeiras (Visa, Mastercard, Elo, etc.) para validar números de cartão.
     *
     * Como funciona:
     *   1. Remove espaços e hífens do número
     *   2. A partir do penúltimo dígito, vai para a esquerda dobrando dígitos alternados
     *   3. Se o dígito dobrado for > 9, subtrai 9
     *   4. Soma todos os dígitos
     *   5. Se a soma for divisível por 10, o número é válido
     *
     * Exemplo: 4532015112830366 (cartão Visa de teste)
     */
    public void validarNumero(String numeroCartao) {
        if (numeroCartao == null || numeroCartao.isBlank()) {
            throw new IllegalArgumentException("Número do cartão é obrigatório");
        }

        // Remove espaços e hífens (ex: "4532 0151 1283 0366" → "4532015112830366")
        String numero = numeroCartao.replaceAll("[\\s-]", "");

        // Cartões têm entre 13 e 19 dígitos
        if (!numero.matches("\\d{13,19}")) {
            throw new IllegalArgumentException("Número do cartão inválido: deve conter entre 13 e 19 dígitos numéricos");
        }

        if (!luhn(numero)) {
            throw new IllegalArgumentException("Número do cartão inválido: falhou na verificação de integridade");
        }
    }

    /**
     * Implementação do Algoritmo de Luhn.
     * Retorna true se o número for válido, false caso contrário.
     */
    private boolean luhn(String numero) {
        int soma = 0;
        boolean dobrar = false;

        // Percorre o número da DIREITA para a ESQUERDA
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(numero.charAt(i));

            if (dobrar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9; // Equivale a somar os dois dígitos do resultado
                }
            }

            soma += digito;
            dobrar = !dobrar; // Alterna a cada dígito
        }

        return soma % 10 == 0;
    }

    // ── Validação da validade ──────────────────────────────────────────────

    /**
     * Valida a data de validade no formato MM/AA.
     * O cartão não pode estar vencido.
     *
     * @param validade string no formato "MM/AA" (ex: "12/27")
     */
    public void validarValidade(String validade) {
        if (validade == null || validade.isBlank()) {
            throw new IllegalArgumentException("Validade do cartão é obrigatória");
        }

        if (!validade.matches("\\d{2}/\\d{2}")) {
            throw new IllegalArgumentException("Validade deve estar no formato MM/AA (ex: 12/27)");
        }

        try {
            // Converte "12/27" para YearMonth para comparação
            YearMonth expiracao = YearMonth.parse(validade, DateTimeFormatter.ofPattern("MM/yy"));
            YearMonth hoje = YearMonth.now();

            if (expiracao.isBefore(hoje)) {
                throw new IllegalArgumentException("Cartão vencido. Validade: " + validade);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Validade inválida: " + validade);
        }
    }

    // ── Validação do CVV ───────────────────────────────────────────────────

    /**
     * Valida o CVV (Card Verification Value).
     *
     * A maioria das bandeiras usa 3 dígitos (Visa, Mastercard, Elo).
     * American Express usa 4 dígitos.
     * Por isso aceitamos 3 ou 4 dígitos.
     */
    public void validarCvv(String cvv) {
        if (cvv == null || cvv.isBlank()) {
            throw new IllegalArgumentException("CVV é obrigatório");
        }

        if (!cvv.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("CVV inválido: deve ter 3 ou 4 dígitos numéricos");
        }
    }

    // ── Identificação de bandeira (bônus) ─────────────────────────────────

    /**
     * Identifica a bandeira do cartão pelo prefixo do número.
     * Útil para exibir o ícone correto no front-end.
     *
     * Não é exaustivo — cobre as principais bandeiras do Brasil.
     */
    public String identificarBandeira(String numeroCartao) {
        String numero = numeroCartao.replaceAll("[\\s-]", "");

        if (numero.matches("^4\\d{12}(\\d{3})?$")) return "VISA";
        if (numero.matches("^5[1-5]\\d{14}$")) return "MASTERCARD";
        if (numero.matches("^3[47]\\d{13}$")) return "AMEX";
        if (numero.matches("^6(362|363|364|365)\\d{12}$")) return "ELO";
        if (numero.matches("^(384100|384140|384160|606282|637095|637568|637599|637609|637612)\\d{10}$")) return "HIPERCARD";

        return "DESCONHECIDA";
    }
}