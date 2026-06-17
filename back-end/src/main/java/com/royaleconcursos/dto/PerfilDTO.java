package com.royaleconcursos.dto;

public class PerfilDTO {
    private String id;
    private String name;
    private String email;
    private String foto;

    public PerfilDTO(String id, String nome, String email, String foto) {
        this.id = id;
        this.name = nome;
        this.email = email;
        this.foto = foto;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getFoto() { return foto; }
}