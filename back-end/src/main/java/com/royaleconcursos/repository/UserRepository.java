
package com.royaleconcursos.repository;

import com.royaleconcursos.model.User;
import org.springframework.data.jpa.repository.JpaRepository; //import automatico para consultas no banco de dados

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    // procura o usuario pelo emai e cpf
    Optional<User> findByEmail(String email);
    Optional<User> findByCpf(String cpf);
}