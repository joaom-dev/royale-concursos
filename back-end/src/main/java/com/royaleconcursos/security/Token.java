package com.royaleconcursos.security;


import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.royaleconcursos.model.User;

@Service
public class Token {    
    
    @Value("${jwt.secret}")
    private String secret;
    
    public String gerarToken(User user){

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret); 

            String token = JWT.create()
                        .withIssuer("royale-concursos") 
                        .withSubject(user.getEmail()) 
                        .withExpiresAt(expirationToken())
                        .sign(algorithm);

                    return token;
        } 
        catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao se autenticar");
        }        
    }

    public String validateToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("royale-concursos")
                    .build()
                    .verify(token)
                    .getSubject();
        }
        catch(JWTVerificationException exception){
            return null;
        }
    }

    private Instant expirationToken(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
