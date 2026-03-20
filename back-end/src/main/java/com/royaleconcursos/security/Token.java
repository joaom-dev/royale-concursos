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

        //tratamento de exceçao
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret); //tipo de criptografia do token e senha de acesso

            //criaçao do token
            String token = JWT.create()
                        .withIssuer("royale-concursos") //quem ta emitindo esse token
                        .withSubject(user.getEmail()) //quem ta ganhando o token
                        .withExpiresAt(expirationToken()) //expiraçao do token
                        .sign(algorithm);

                    return token;
        } 
        catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao se autenticar");
        }        
    }
    //validaçao do token
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

    //tempo de expiraçao do token
    private Instant expirationToken(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
