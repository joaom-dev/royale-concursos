//recriaçao da tabela de usuarios do banco de dados
package com.royaleconcursos.model;

import com.royaleconcursos.enums.Plano;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity // diz que esta classe e uma entidade
@Table(name = "users") // usar a tabela users do banco de dados
@Getter // automatizar o uso do get e set
@Setter // automatizar o uso do get e set
@AllArgsConstructor
@NoArgsConstructor

public class User {
    // criaçao da entidade de usuario
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_user")
    private String id;

    @Column(name = "name_user")
    private String name;

    @Column(name = "cpf_user", unique = true)
    private String cpf;

    @Column(name = "email_user", unique = true)
    private String email;

    @Column(name = "password_user")
    private String password;

    // ── Campos de plano (usados pelo PagamentoService/PlanoService) ─────────

    @Enumerated(EnumType.STRING)
    @Column(name = "plano")
    private Plano plano;

    @Column(name = "plano_expira_em")
    private LocalDateTime planoExpiraEm;

    /**
     * Garante que usuários recém-criados comecem como FREE.
     */
    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (plano == null) {
            plano = Plano.FREE;
        }
    }

    /**
     * Verifica se o plano MENSAL ainda está ativo (não expirou).
     * Usado pelo PlanoService.getPlanoEfetivo().
     */
    public boolean isPlanoAtivo() {
        if (plano != Plano.MENSAL) {
            return true; // FREE e VITALICIO não dependem de planoExpiraEm
        }
        return planoExpiraEm != null && LocalDateTime.now().isBefore(planoExpiraEm);
    }
}
