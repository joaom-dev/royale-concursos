package com.royaleconcursos.dto;



import java.time.LocalDateTime;

// DTO usado para retornar os dados de concurso ao front-end.
//Evita expor a entidade JPA diretamente.

 
public class ConcursoDTO {

    private Long id;
    private String orgao;
    private String situacao;
    private String uf;
    private String estado;
    private String tipo;
    private String vagas;
    private String link;
    private LocalDateTime atualizadoEm;

    // Construtor vazio
    public ConcursoDTO() {}

    // Construtor completo (útil para mapear a partir da entidade)
    public ConcursoDTO(Long id, String orgao, String situacao, String uf,
                       String estado, String tipo, String vagas,
                       String link, LocalDateTime atualizadoEm) {
        this.id = id;
        this.orgao = orgao;
        this.situacao = situacao;
        this.uf = uf;
        this.estado = estado;
        this.tipo = tipo;
        this.vagas = vagas;
        this.link = link;
        this.atualizadoEm = atualizadoEm;
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

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
