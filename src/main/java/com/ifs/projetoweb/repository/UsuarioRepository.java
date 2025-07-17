package com.ifs.projetoweb.repository;


import com.ifs.projetoweb.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.*;

import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByMatricula(String matricula);
    Optional<Usuario> findByEmail(String email);

}
