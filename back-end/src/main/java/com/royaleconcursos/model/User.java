//recriaçao da tabela de usuarios do banco de dados
package com.royaleconcursos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
