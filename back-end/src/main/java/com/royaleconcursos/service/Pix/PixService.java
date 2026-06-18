package com.royaleconcursos.service.pix;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.zip.CRC32;

@Service
public class PixService {

    private static final String PAYLOAD_FORMAT_INDICATOR = "01";
    private static final String MERCHANT_ACCOUNT_INFO_GUI = "br.gov.bcb.pix";
    private static final String MERCHANT_CATEGORY_CODE = "0000";
    private static final String TRANSACTION_CURRENCY_BRL = "986";
    private static final String COUNTRY_CODE = "BR";

    /**
     @param chavePix    
     @param nomeRecebedor 
     @param cidade      
     @param valor       
     @param txId        
     @return 
     */
    public String gerarPayload(String chavePix, String nomeRecebedor, String cidade, BigDecimal valor, String txId) {
        String nome = limitarTamanho(normalizar(nomeRecebedor), 25);
        String cidadeFormatada = limitarTamanho(normalizar(cidade), 15);
        String transacaoId = limitarTamanho(normalizarTxId(txId), 25);
        String valorFormatado = valor.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();

        StringBuilder payload = new StringBuilder();

        payload.append(campo("00", PAYLOAD_FORMAT_INDICATOR));

        String merchantAccountInfo =
                campo("00", MERCHANT_ACCOUNT_INFO_GUI) +
                campo("01", chavePix);
        payload.append(campo("26", merchantAccountInfo));

        payload.append(campo("52", MERCHANT_CATEGORY_CODE));

        payload.append(campo("53", TRANSACTION_CURRENCY_BRL));

        payload.append(campo("54", valorFormatado));

        payload.append(campo("58", COUNTRY_CODE));

        payload.append(campo("59", nome));

        payload.append(campo("60", cidadeFormatada));

        String additionalData = campo("05", transacaoId.isEmpty() ? "***" : transacaoId);
        payload.append(campo("62", additionalData));

        payload.append("6304");
        String crc = calcularCRC16(payload.toString());

        return payload + crc;
    }

    private String campo(String id, String valor) {
        String tamanho = String.format("%02d", valor.length());
        return id + tamanho + valor;
    }

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

    private String normalizar(String texto) {
        if (texto == null) return "";
        String normalizado = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD);
        return normalizado.replaceAll("[^\\p{ASCII}]", "");
    }

    private String normalizarTxId(String txId) {
        if (txId == null) return "";
        return txId.replaceAll("[^a-zA-Z0-9]", "");
    }

    private String limitarTamanho(String texto, int max) {
        if (texto == null) return "";
        return texto.length() > max ? texto.substring(0, max) : texto;
    }
}
