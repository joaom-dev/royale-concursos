package com.royaleconcursos.service;

import com.royaleconcursos.dto.AtualizarPerfilDTO;
import com.royaleconcursos.dto.PerfilDTO;
import com.royaleconcursos.model.User;
import com.royaleconcursos.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class PerfilService {

    @Autowired
    private UserRepository userRepository;

    // Busca e retorna os dados do perfil do usuário pelo e-mail
    public PerfilDTO buscarPerfil(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return new PerfilDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getFoto()
        );
    }

    // Atualiza nome e/ou e-mail do usuário
    public void atualizarPerfil(String emailAtual, AtualizarPerfilDTO dto) {
        User user = userRepository.findByEmail(emailAtual)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Atualiza o nome se foi informado
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        // Atualiza o e-mail se foi informado e é diferente do atual
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && !dto.getEmail().equalsIgnoreCase(emailAtual)) {

            // Verifica se o novo e-mail já está em uso por outro usuário
            boolean emailEmUso = userRepository.findByEmail(dto.getEmail()).isPresent();
            if (emailEmUso) {
                throw new RuntimeException("Este e-mail já está em uso por outro usuário");
            }
            user.setEmail(dto.getEmail());
        }

        userRepository.save(user);
    }

    // Salva a foto de perfil em disco e atualiza o caminho no banco
    public String salvarFoto(String email, MultipartFile arquivo) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Cria o diretório de upload se não existir
        String diretorio = "upload/fotos/";
        File dir = new File(diretorio);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Gera um nome único para o arquivo
        String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
        String caminhoCompleto = diretorio + nomeArquivo;

        arquivo.transferTo(new File(caminhoCompleto));

        // Salva a URL pública no banco (servida pelo ResourceHandler)
        String urlPublica = "/fotos/" + nomeArquivo;
        user.setFoto(urlPublica);
        userRepository.save(user);

        return urlPublica;
    }
}