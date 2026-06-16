package com.royaleconcursos.service;

import com.royaleconcursos.dto.PerfilDTO;
import com.royaleconcursos.model.User;
import com.royaleconcursos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class PerfilService {
    private static final String UPLOAD_DIR = "upload/fotos/";

    @Autowired
    private UserRepository userRepository;

    public PerfilDTO buscarPerfil (String email) {
        User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));
        return new PerfilDTO(user.getId(), user.getName(),
         user.getEmail(), user.getFoto());
    }

    public String salvarFoto (String email, MultipartFile arquivo) throws IOException { 
        User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Usuario não encontrado"));

        String nomeOriginal = arquivo.getOriginalFilename();
        if (nomeOriginal == null || !nomeOriginal.contains(".")) {
            throw new RuntimeException("Arquivo Inválido");
        }

        if (arquivo.isEmpty()) {
                    throw new RuntimeException("Arquivo Vazio");
        }

        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));

        String nomeArquivo = UUID.randomUUID().toString() + extensao;

        
        Path caminho = Paths.get(UPLOAD_DIR + nomeArquivo);
        Files.createDirectories(caminho.getParent());
        Files.write(caminho, arquivo.getBytes());

        user.setFoto("/fotos/" + nomeArquivo);
        userRepository.save(user);

        return "/fotos/" + nomeArquivo;
    }
}
