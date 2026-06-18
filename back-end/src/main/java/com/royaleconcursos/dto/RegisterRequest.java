package com.royaleconcursos.dto;

public record RegisterRequest (String name, String cpf, String email, String password) {
}