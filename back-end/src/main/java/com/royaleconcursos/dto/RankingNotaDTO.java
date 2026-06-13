package com.royaleconcursos.dto;

import java.time.LocalDateTime;

public class RankingNotaDTO {

    private Long id;
    private Long concursoId;
    private String nome;
    private Double nota;
    private String fotoUrl;
    private LocalDateTime criadoEm;

    public RankingNotaDTO() {}

    public RankingNotaDTO(Long id, Long concursoId, String nome,
                          Double nota, String fotoUrl, LocalDateTime criadoEm) {
        this.id         = id;
        this.concursoId = concursoId;
        this.nome       = nome;
        this.nota       = nota;
        this.fotoUrl    = fotoUrl;
        this.criadoEm   = criadoEm;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getConcursoId() { return concursoId; }
    public void setConcursoId(Long concursoId) { this.concursoId = concursoId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}