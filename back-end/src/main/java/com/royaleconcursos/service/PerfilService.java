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

    public PerfilDTO buscarPerfil(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return new PerfilDTO(user.getId(), user.getName(), user.getEmail(), user.getFoto());
    }

    public void atualizarPerfil(String emailAtual, AtualizarPerfilDTO dto) {
        User user = userRepository.findByEmail(emailAtual)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && !dto.getEmail().equalsIgnoreCase(emailAtual)) {
            boolean emailEmUso = userRepository.findByEmail(dto.getEmail()).isPresent();
            if (emailEmUso) {
                throw new RuntimeException("Este e-mail já está em uso por outro usuário");
            }
            user.setEmail(dto.getEmail());
        }

        userRepository.save(user);
    }

    public String salvarFoto(String email, MultipartFile arquivo) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String diretorio = "upload/fotos/";
        File dir = new File(diretorio);
        if (!dir.exists()) dir.mkdirs();

        String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
        arquivo.transferTo(new File(diretorio + nomeArquivo));

        String urlPublica = "/fotos/" + nomeArquivo;
        user.setFoto(urlPublica);
        userRepository.save(user);

        return urlPublica;
    }

    public void excluirConta(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        userRepository.delete(user);
    }
}