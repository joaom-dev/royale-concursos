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

@Entity
@Table(name = "users") 
@Getter 
@Setter 
@AllArgsConstructor
@NoArgsConstructor

public class User {
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

    @Column(name = "foto_perfil")
    private String foto;

    
    @Column(nullable = false)
    private String role = "User";

    @Enumerated(EnumType.STRING)
    @Column(name = "plano")
    private Plano plano;

    @Column(name = "plano_expira_em")
    private LocalDateTime planoExpiraEm;

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (plano == null) {
            plano = Plano.FREE;
        }
    }

    public boolean isPlanoAtivo() {
        if (plano != Plano.MENSAL) {
            return true;
        }
        return planoExpiraEm != null && LocalDateTime.now().isBefore(planoExpiraEm);
    }
}
