package com.royaleconcursos.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    // Pasta física onde os arquivos ficam salvos no servidor.
    // Já existe um ResourceHandler em SecurityConfig.java mapeando "upload/fotos/" -> "/fotos/**"
    private static final String PASTA_BASE = "upload/fotos/anuncios/";

    private static final Set<String> EXTENSOES_PERMITIDAS = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final long TAMANHO_MAXIMO_BYTES = 5L * 1024 * 1024; // 5MB

    /**
     * Salva o arquivo de imagem enviado e devolve a URL relativa para salvar no banco.
     * Ex: "/fotos/anuncios/3f2a1b9c.jpg"
     */
    public String salvarImagemAnuncio(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma imagem foi enviada.");
        }

        if (arquivo.getSize() > TAMANHO_MAXIMO_BYTES) {
            throw new IllegalArgumentException("Imagem maior que o limite permitido (5MB).");
        }

        String nomeOriginal = arquivo.getOriginalFilename();
        String extensao = extrairExtensao(nomeOriginal);

        if (!EXTENSOES_PERMITIDAS.contains(extensao.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Tipo de arquivo não permitido. Use: " + EXTENSOES_PERMITIDAS);
        }

        try {
            // ✅ toAbsolutePath() é obrigatório: MultipartFile.transferTo(Path) exige caminho absoluto
            Path pastaDestino = Paths.get(PASTA_BASE).toAbsolutePath();
            Files.createDirectories(pastaDestino);

            // Nome único pra evitar sobrescrever arquivos com mesmo nome
            String nomeArquivo = UUID.randomUUID() + "." + extensao;
            Path caminhoCompleto = pastaDestino.resolve(nomeArquivo);

            arquivo.transferTo(caminhoCompleto);

            // Essa é a URL relativa que vai pro banco (campo imagemUrl)
            return "/fotos/anuncios/" + nomeArquivo;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar a imagem do anúncio", e);
        }
    }

    private String extrairExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            throw new IllegalArgumentException("Arquivo sem extensão válida.");
        }
        return nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1);
    }
}