package com.royaleconcursos.dto;

public class PerfilDTO {
    private long id;
    private String nome;
    private String email;
    private String foto_perfil;

    public PerfilDTO(long id,String nome, String email, String foto_perfil) {
        this.email = email;
        this.foto_perfil = foto_perfil;
        this.id = id; 
        this.nome = nome;
    }

    public long getId() {return id;}
    public String getNome() {return nome;}
    public String getEmail() {return email;}
    public String getFoto_perfil() {return foto_perfil;}


}
