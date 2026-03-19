package com.royaleconcursos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    //sistema de segurança que permite so o registro e o login ficar sem autenticaçao, todas as outras rotas vao precisar.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/usuarios/register").permitAll()
                .requestMatchers("/usuarios/login").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}