package com.royaleconcursos.dto;

public record LoginRequest (String email, String password){
    //record cria uma classe simples para receber dados da API
}
//recebe os dados enviados pelo frontend