package com.ifs.projetoweb.repository;



import com.ifs.projetoweb.entity.Competicao;
import com.ifs.projetoweb.entity.Curso;
import com.ifs.projetoweb.entity.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    Optional<Equipe> findByCursoAndCompeticao(Curso curso, Competicao competicao);
    List<Equipe> findByCompeticao(Competicao competicao);
}
