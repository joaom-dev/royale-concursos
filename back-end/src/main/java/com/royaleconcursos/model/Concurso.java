package com.royaleconcursos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "concursos")
public class Concurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orgao", nullable = false)
    private String orgao;

    @Column(name = "situacao")
    private String situacao;

    @Column(name = "uf", length = 2, nullable = false)
    private String uf;

    @Column(name = "estado")
    private String estado;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "vagas")
    private String vagas;

    @Column(name = "link", length = 1000)
    private String link;

    @Column(name = "edital_url", length = 1000)
    private String editalUrl;

    @Column(name = "periodo_inscricao")
    private String periodoInscricao;

    @Column(name = "nivel")
    private String nivel;

    @Column(name = "banca")
    private String banca;

    @Column(name = "cargo", length = 1000)
    private String cargo;

    @Column(name = "salario")
    private String salario;

    @Column(name = "requisitos", length = 3000)
    private String requisitos;

    @Column(name = "beneficios", length = 2000)
    private String beneficios;

    @Column(name = "carga_horaria")
    private String cargaHoraria;

    @Column(name = "observacao", length = 2000)
    private String observacao;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    // ─── Getters e Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrgao() { return orgao; }
    public void setOrgao(String orgao) { this.orgao = orgao; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getVagas() { return vagas; }
    public void setVagas(String vagas) { this.vagas = vagas; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getEditalUrl() { return editalUrl; }
    public void setEditalUrl(String editalUrl) { this.editalUrl = editalUrl; }

    public String getPeriodoInscricao() { return periodoInscricao; }
    public void setPeriodoInscricao(String periodoInscricao) { this.periodoInscricao = periodoInscricao; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getBanca() { return banca; }
    public void setBanca(String banca) { this.banca = banca; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getSalario() { return salario; }
    public void setSalario(String salario) { this.salario = salario; }

    public String getRequisitos() { return requisitos; }
    public void setRequisitos(String requisitos) { this.requisitos = requisitos; }

    public String getBeneficios() { return beneficios; }
    public void setBeneficios(String beneficios) { this.beneficios = beneficios; }

    public String getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(String cargaHoraria) { this.cargaHoraria = cargaHoraria; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}