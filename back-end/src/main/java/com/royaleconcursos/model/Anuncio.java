package com.royaleconcursos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "anuncios")
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ✅ Long (wrapper) em vez de long (primitivo) — evita NPE

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String imagemUrl;
    private String linkDestino;
    private String posicao; // topo, lateral, rodapé

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    @Column(nullable = false)
    private boolean ativo = true;

    public Long getId()                    { return id; }
    public String getTitulo()              { return titulo; }
    public String getDescricao()           { return descricao; }
    public String getImagemUrl()           { return imagemUrl; }
    public String getLinkDestino()         { return linkDestino; }
    public String getPosicao()             { return posicao; }
    public LocalDateTime getDataInicio()   { return dataInicio; }
    public LocalDateTime getDataFim()      { return dataFim; }
    public boolean isAtivo()               { return ativo; }

    public void setId(Long id)                         { this.id = id; }
    public void setTitulo(String titulo)               { this.titulo = titulo; }
    public void setDescricao(String descricao)         { this.descricao = descricao; }
    public void setImagemUrl(String imagemUrl)         { this.imagemUrl = imagemUrl; }
    public void setLinkDestino(String linkDestino)     { this.linkDestino = linkDestino; }
    public void setPosicao(String posicao)             { this.posicao = posicao; }
    public void setDataInicio(LocalDateTime dataInicio){ this.dataInicio = dataInicio; }
    public void setDataFim(LocalDateTime dataFim)      { this.dataFim = dataFim; }
    public void setAtivo(boolean ativo)                { this.ativo = ativo; }
}