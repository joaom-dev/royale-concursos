package com.royaleconcursos.service.pix;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.zip.CRC32;

/**
 * Serviço responsável por gerar o payload PIX ("copia e cola")
 * no padrão BR Code (EMV / BACEN).
 *
 * Gera um payload PIX ESTÁTICO (com valor fixo).
 * Não envolve nenhuma chamada externa — é montado localmente
 * seguindo a especificação do BACEN.
 */
@Service
public class PixService {

    private static final String PAYLOAD_FORMAT_INDICATOR = "01";
    private static final String MERCHANT_ACCOUNT_INFO_GUI = "br.gov.bcb.pix";
    private static final String MERCHANT_CATEGORY_CODE = "0000";
    private static final String TRANSACTION_CURRENCY_BRL = "986";
    private static final String COUNTRY_CODE = "BR";

    /**
     * Gera o payload PIX completo (com CRC16) para um pagamento.
     *
     * @param chavePix    chave PIX do recebedor (e-mail, CPF, telefone ou chave aleatória)
     * @param nomeRecebedor nome do beneficiário (máx. 25 caracteres)
     * @param cidade      cidade do beneficiário (máx. 15 caracteres)
     * @param valor       valor da transação
     * @param txId        identificador da transação (até 25 caracteres alfanuméricos)
     * @return string do payload PIX pronto para gerar QR Code / copiar e colar
     */
    public String gerarPayload(String chavePix, String nomeRecebedor, String cidade, BigDecimal valor, String txId) {
        String nome = limitarTamanho(normalizar(nomeRecebedor), 25);
        String cidadeFormatada = limitarTamanho(normalizar(cidade), 15);
        String transacaoId = limitarTamanho(normalizarTxId(txId), 25);
        String valorFormatado = valor.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();

        StringBuilder payload = new StringBuilder();

        // 00 - Payload Format Indicator
        payload.append(campo("00", PAYLOAD_FORMAT_INDICATOR));

        // 26 - Merchant Account Information (Arranjo PIX)
        String merchantAccountInfo =
                campo("00", MERCHANT_ACCOUNT_INFO_GUI) +
                campo("01", chavePix);
        payload.append(campo("26", merchantAccountInfo));

        // 52 - Merchant Category Code
        payload.append(campo("52", MERCHANT_CATEGORY_CODE));

        // 53 - Transaction Currency (986 = BRL)
        payload.append(campo("53", TRANSACTION_CURRENCY_BRL));

        // 54 - Transaction Amount
        payload.append(campo("54", valorFormatado));

        // 58 - Country Code
        payload.append(campo("58", COUNTRY_CODE));

        // 59 - Merchant Name
        payload.append(campo("59", nome));

        // 60 - Merchant City
        payload.append(campo("60", cidadeFormatada));

        // 62 - Additional Data Field Template (Txid)
        String additionalData = campo("05", transacaoId.isEmpty() ? "***" : transacaoId);
        payload.append(campo("62", additionalData));

        // 63 - CRC16 (calculado sobre o payload + "6304")
        payload.append("6304");
        String crc = calcularCRC16(payload.toString());

        return payload + crc;
    }

    /**
     * Monta um campo no formato TLV (Tag-Length-Value) usado pelo padrão EMV.
     * Ex.: campo("00", "01") → "000201"
     */
    private String campo(String id, String valor) {
        String tamanho = String.format("%02d", valor.length());
        return id + tamanho + valor;
    }

    /**
     * Calcula o CRC16-CCITT (polinômio 0x1021), exigido pelo padrão PIX.
     * Retorna o valor em hexadecimal, 4 dígitos, maiúsculo.
     */
    private String calcularCRC16(String payload) {
        int polinomio = 0x1021;
        int resultado = 0xFFFF;

        byte[] bytes = payload.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        for (byte b : bytes) {
            resultado ^= (b << 8) & 0xFF00;
            for (int i = 0; i < 8; i++) {
                if ((resultado & 0x8000) != 0) {
                    resultado = (resultado << 1) ^ polinomio;
                } else {
                    resultado = resultado << 1;
                }
                resultado &= 0xFFFF;
            }
        }

        return String.format("%04X", resultado);
    }

    /**
     * Remove acentos e caracteres especiais (o padrão PIX exige apenas
     * caracteres ASCII no nome/cidade do beneficiário).
     */
    private String normalizar(String texto) {
        if (texto == null) return "";
        String normalizado = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD);
        return normalizado.replaceAll("[^\\p{ASCII}]", "");
    }

    /**
     * Remove caracteres inválidos do txId (apenas alfanuméricos são permitidos).
     */
    private String normalizarTxId(String txId) {
        if (txId == null) return "";
        return txId.replaceAll("[^a-zA-Z0-9]", "");
    }

    private String limitarTamanho(String texto, int max) {
        if (texto == null) return "";
        return texto.length() > max ? texto.substring(0, max) : texto;
    }
}
