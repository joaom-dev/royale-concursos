package com.royaleconcursos.dto;

import java.time.LocalDateTime;

public class AnuncioDTO {

    private String titulo;
    private String descricao;
    private String imagemUrl;
    private String linkDestino;
    private String posicao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private boolean ativo;

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public String getDescricao() {return descricao;}
    public void setDescricao(String descricao) {this.descricao = descricao;}

    public String getImagemUrl() {return imagemUrl;}
    public void setImagemUrl(String imagemUrl) {this.imagemUrl = imagemUrl;}

    public String getLinkDestino() {return linkDestino;}
    public void setLinkDestino(String linkDestino) {this.linkDestino = linkDestino;}
    
    public String getPosicao() {return posicao;}
    public void setPosicao(String posicao) {this.posicao = posicao;}

    public LocalDateTime getDataInicio() {return dataInicio;}
    public void setDataInicio(LocalDateTime dataInicio) {this.dataInicio = dataInicio;}

    public LocalDateTime getDataFim() {return dataFim;}
    public void setDataFim(LocalDateTime dataFim) {this.dataFim = dataFim;}

    public boolean isAtivo() {return ativo;}
    public void setAtivo(boolean ativo) {this.ativo = ativo;}


    
}