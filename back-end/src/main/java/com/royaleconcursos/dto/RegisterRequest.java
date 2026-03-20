package com.royaleconcursos.dto;

public record RegisterRequest (String name, String cpf, String email, String password) {
    //record cria uma classe simples para receber dados da API
}
//rece os dados da tela de cadastro 