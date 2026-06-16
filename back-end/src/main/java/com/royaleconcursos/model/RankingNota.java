package com.royaleconcursos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ranking_notas",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"concurso_id", "cpf"},
        name = "uq_ranking_cpf_concurso"
    )
)
public class RankingNota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concurso_id", nullable = false)
    private Long concursoId;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 14)
    private String cpf;

    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false)
    private Double nota;

    @Column(name = "foto_url", columnDefinition = "TEXT")
    private String fotoUrl;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Long getId() { return id; }
    public Long getConcursoId() { return concursoId; }
    public void setConcursoId(Long concursoId) { this.concursoId = concursoId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Double getNota() { return nota; }
    public void setNota(Double nota) { this.nota = nota; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}