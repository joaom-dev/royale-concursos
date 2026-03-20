
package com.royaleconcursos.service;

import com.royaleconcursos.dto.LoginRequest;
import com.royaleconcursos.dto.RegisterRequest;
import com.royaleconcursos.dto.Response;
import com.royaleconcursos.model.User;
import com.royaleconcursos.repository.UserRepository;
import com.royaleconcursos.security.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final Token tokenService;

    public Response register(RegisterRequest body) {

        Optional<User> user = repository.findByEmail(body.email());

        if (user.isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (repository.findByCpf(body.cpf()).isPresent()) {
            throw new RuntimeException("CPF already registered");
        }

        User newUser = new User();

        newUser.setName(body.name());
        newUser.setCpf(body.cpf());
        newUser.setEmail(body.email());
        newUser.setPassword(passwordEncoder.encode(body.password()));

        repository.save(newUser);

        String token = tokenService.gerarToken(newUser);

        return new Response(newUser.getName(), token);
    }

    public Response login(LoginRequest body) {

        User user = repository.findByEmail(body.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(body.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = tokenService.gerarToken(user);

        return new Response(user.getName(), token);
    }
}
