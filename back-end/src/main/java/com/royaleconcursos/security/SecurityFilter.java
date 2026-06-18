package com.royaleconcursos.security;

import com.royaleconcursos.model.User;
import com.royaleconcursos.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    Token tokenService;
    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = recoverToken(request);
        System.out.println("[DEBUG] TOKEN RECEBIDO: " + token);

        if (token != null) {
            String login = tokenService.validateToken(token);
            System.out.println("[DEBUG] LOGIN EXTRAIDO DO TOKEN: " + login);

            if (login != null) {
                User user = userRepository.findByEmail(login)
                        .orElseThrow(() -> new RuntimeException("User Not Found"));

                System.out.println("[DEBUG] ROLE NO BANCO: [" + user.getRole() + "]");

                String role = "ROLE_" + user.getRole().toUpperCase();
                System.out.println("[DEBUG] AUTHORITY GERADA: " + role);

                var authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
                var authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("[DEBUG] AUTENTICACAO SETADA COM SUCESSO PARA: " + user.getEmail());
            } else {
                System.out.println("[DEBUG] TOKEN INVALIDO OU EXPIRADO - login veio null");
            }
        } else {
            System.out.println("[DEBUG] NENHUM TOKEN ENCONTRADO NO HEADER AUTHORIZATION");
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null)
            return null;
        return authHeader.replace("Bearer ", "");
    }
}