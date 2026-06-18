package com.royaleconcursos.dto;

import java.time.LocalDateTime;

public class ConcursoDTO {

    private Long id;
    private String orgao;
    private String situacao;
    private String uf;
    private String estado;
    private String tipo;
    private String vagas;
    private String link;
    private String editalUrl;
    private String periodoInscricao;
    private String nivel;
    private String banca;
    private String cargo;
    private String salario;
    private String requisitos;
    private String beneficios;
    private String cargaHoraria;
    private String observacao;
    private LocalDateTime atualizadoEm;

    public ConcursoDTO() {}

    public ConcursoDTO(Long id, String orgao, String situacao, String uf,
                       String estado, String tipo, String vagas, String link,
                       String editalUrl, String periodoInscricao, String nivel,
                       String banca, String cargo, String salario,
                       String requisitos, String beneficios, String cargaHoraria,
                       String observacao, LocalDateTime atualizadoEm) {
        this.id               = id;
        this.orgao            = orgao;
        this.situacao         = situacao;
        this.uf               = uf;
        this.estado           = estado;
        this.tipo             = tipo;
        this.vagas            = vagas;
        this.link             = link;
        this.editalUrl        = editalUrl;
        this.periodoInscricao = periodoInscricao;
        this.nivel            = nivel;
        this.banca            = banca;
        this.cargo            = cargo;
        this.salario          = salario;
        this.requisitos       = requisitos;
        this.beneficios       = beneficios;
        this.cargaHoraria     = cargaHoraria;
        this.observacao       = observacao;
        this.atualizadoEm     = atualizadoEm;
    }

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