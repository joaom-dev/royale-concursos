package com.royaleconcursos.validator;

import com.royaleconcursos.enums.MetodoPagamento;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class CartaoValidator {

    public void validar(String numeroCartao, String validade, String cvv, MetodoPagamento metodo) {
        if (metodo != MetodoPagamento.CARTAO_CREDITO && metodo != MetodoPagamento.CARTAO_DEBITO) {
            return; // Não é cartão, não precisa validar
        }

        validarNumero(numeroCartao);
        validarValidade(validade);
        validarCvv(cvv);
    }

    public void validarNumero(String numeroCartao) {
        if (numeroCartao == null || numeroCartao.isBlank()) {
            throw new IllegalArgumentException("Número do cartão é obrigatório");
        }

        String numero = numeroCartao.replaceAll("[\\s-]", "");

        if (!numero.matches("\\d{13,19}")) {
            throw new IllegalArgumentException("Número do cartão inválido: deve conter entre 13 e 19 dígitos numéricos");
        }

        if (!luhn(numero)) {
            throw new IllegalArgumentException("Número do cartão inválido: falhou na verificação de integridade");
        }
    }

    private boolean luhn(String numero) {
        int soma = 0;
        boolean dobrar = false;

        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(numero.charAt(i));

            if (dobrar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9; 
                }
            }

            soma += digito;
            dobrar = !dobrar;
        }

        return soma % 10 == 0;
    }


    /**
     * @param validade
     */
    public void validarValidade(String validade) {
        if (validade == null || validade.isBlank()) {
            throw new IllegalArgumentException("Validade do cartão é obrigatória");
        }

        if (!validade.matches("\\d{2}/\\d{2}")) {
            throw new IllegalArgumentException("Validade deve estar no formato MM/AA (ex: 12/27)");
        }

        try {
            YearMonth expiracao = YearMonth.parse(validade, DateTimeFormatter.ofPattern("MM/yy"));
            YearMonth hoje = YearMonth.now();

            if (expiracao.isBefore(hoje)) {
                throw new IllegalArgumentException("Cartão vencido. Validade: " + validade);
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Validade inválida: " + validade);
        }
    }

    public void validarCvv(String cvv) {
        if (cvv == null || cvv.isBlank()) {
            throw new IllegalArgumentException("CVV é obrigatório");
        }

        if (!cvv.matches("\\d{3,4}")) {
            throw new IllegalArgumentException("CVV inválido: deve ter 3 ou 4 dígitos numéricos");
        }
    }

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