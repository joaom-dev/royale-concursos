package com.royaleconcursos.service.pix;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.zip.CRC32;

/**
 * Gerador de payload PIX (BR Code) seguindo o padrão do Banco Central do Brasil.
 *
 * O que este serviço faz:
 *   1. Monta o payload de texto no formato EMV (padrão internacional de pagamentos)
 *   2. Calcula o CRC16 (checksum de integridade exigido pelo BACEN)
 *   3. Retorna o código "Copia e Cola" que pode ser transformado em QR Code
 *
 * Para gerar o QR Code de imagem a partir do payload, use a biblioteca ZXing
 * (já inclusa na dependência sugerida no pom.xml) ou envie o payload para
 * o front-end e gere a imagem lá com uma biblioteca JavaScript.
 *
 * Documentação oficial: https://www.bcb.gov.br/content/estabilidadefinanceira/pix/Regulamento_Pix/II_ManualdePadroesparaIniciacaodoPix.pdf
 */
@Service
public class PixService {

    /**
     * Gera o payload PIX (Copia e Cola) completo.
     *
     * @param chavePix   Chave PIX do recebedor (CPF, email, telefone ou chave aleatória)
     * @param nomeLoja   Nome do recebedor que aparece no app de pagamento (max 25 chars)
     * @param cidade     Cidade do recebedor (max 15 chars)
     * @param valor      Valor da cobrança. Se null, gera PIX sem valor (usuário digita)
     * @param txId       Identificador único da transação (max 25 chars, sem espaços)
     * @return           String do payload que pode ser transformada em QR Code
     */
    public String gerarPayload(String chavePix, String nomeLoja, String cidade,
                               BigDecimal valor, String txId) {
        // Sanitiza os campos para evitar caracteres especiais no payload
        nomeLoja = sanitizar(nomeLoja, 25);
        cidade   = sanitizar(cidade, 15);
        txId     = sanitizar(txId, 25).replaceAll("\\s", ""); // sem espaços

        StringBuilder payload = new StringBuilder();

        // ── Campos obrigatórios do EMV ──────────────────────────────────────

        // ID 00: Payload Format Indicator — sempre "01"
        payload.append(campo("00", "01"));

        // ID 26: Merchant Account Information (dados do recebedor PIX)
        String merchantInfo = campo("00", "BR.GOV.BCB.PIX") // GUI do PIX no BACEN
                            + campo("01", chavePix);         // chave PIX
        payload.append(campo("26", merchantInfo));

        // ID 52: Merchant Category Code — "0000" = genérico
        payload.append(campo("52", "0000"));

        // ID 53: Transaction Currency — "986" = BRL (código ISO 4217)
        payload.append(campo("53", "986"));

        // ID 54: Valor (opcional — se null, usuário digita no app)
        if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
            // Formato: sem símbolo, ponto como separador decimal, 2 casas
            // Exemplo: 150.00, 1234.50
            String valorStr = String.format("%.2f", valor);
            payload.append(campo("54", valorStr));
        }

        // ID 58: Country Code — "BR"
        payload.append(campo("58", "BR"));

        // ID 59: Merchant Name (nome do recebedor)
        payload.append(campo("59", nomeLoja));

        // ID 60: Merchant City (cidade do recebedor)
        payload.append(campo("60", cidade));

        // ID 62: Additional Data Field (TX ID para rastrear a transação)
        String additionalData = campo("05", txId.isEmpty() ? "***" : txId);
        payload.append(campo("62", additionalData));

        // ID 63: CRC16 — checksum calculado sobre todo o payload + "6304"
        // Os 4 últimos caracteres são o CRC, então adicionamos o campo vazio
        // e calculamos o CRC sobre o payload completo incluindo "6304"
        payload.append("6304");
        String crc = calcularCRC16(payload.toString());
        payload.append(crc);

        return payload.toString();
    }

    // ── Formato de campo EMV ───────────────────────────────────────────────

    /**
     * Formata um campo EMV no padrão ID + LENGTH + VALUE.
     *
     * Exemplo: campo("59", "Minha Loja") → "5910Minha Loja"
     *   "59" = ID do campo
     *   "10" = tamanho do valor em 2 dígitos
     *   "Minha Loja" = valor
     */
    private String campo(String id, String valor) {
        return id + String.format("%02d", valor.length()) + valor;
    }

    // ── CRC16/CCITT ────────────────────────────────────────────────────────

    /**
     * Calcula o CRC16 no padrão CCITT-FALSE (polinômio 0x1021, valor inicial 0xFFFF).
     * Exigido pelo Banco Central para validar a integridade do payload PIX.
     *
     * Retorna 4 caracteres hexadecimais em maiúsculas (ex: "A3F1").
     */
    private String calcularCRC16(String payload) {
        int crc = 0xFFFF;                  // valor inicial
        int polinomio = 0x1021;            // polinômio CRC-CCITT

        byte[] bytes = payload.getBytes();
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polinomio;
            }
        }

        crc &= 0xFFFF; // garante 16 bits

        // Retorna 4 caracteres hexadecimais maiúsculos com zeros à esquerda
        return String.format("%04X", crc);
    }

    // ── Sanitização ────────────────────────────────────────────────────────

    /**
     * Remove caracteres especiais e acentos do texto (o payload PIX aceita
     * apenas caracteres ASCII imprimíveis) e limita o tamanho.
     */
    private String sanitizar(String texto, int tamanhoMax) {
        if (texto == null) return "";
        String sanitizado = texto
            .replaceAll("[áàãâä]", "a").replaceAll("[ÁÀÃÂÄ]", "A")
            .replaceAll("[éèêë]", "e").replaceAll("[ÉÈÊË]", "E")
            .replaceAll("[íìîï]", "i").replaceAll("[ÍÌÎÏ]", "I")
            .replaceAll("[óòõôö]", "o").replaceAll("[ÓÒÕÔÖ]", "O")
            .replaceAll("[úùûü]", "u").replaceAll("[ÚÙÛÜ]", "U")
            .replaceAll("[ç]", "c").replaceAll("[Ç]", "C")
            .replaceAll("[^\\x20-\\x7E]", ""); // remove tudo fora do ASCII imprimível

        return sanitizado.length() > tamanhoMax
            ? sanitizado.substring(0, tamanhoMax)
            : sanitizado;
    }
}