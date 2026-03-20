package com.royaleconcursos.dto;

public record Response (String name, String token){ 
    //record cria uma classe simples para receber dados da API
}
//response vai definir a resposta da API, no caso o nome e o token