package com.ifs.projetoweb.repository;


import com.ifs.projetoweb.entity.Competicao;
import com.ifs.projetoweb.entity.Grupo;
import com.ifs.projetoweb.entity.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JogoRepository extends JpaRepository<Jogo, Long> {
    List<Jogo> findByGrupo(Grupo grupo);
    List<Jogo> findByGrupoIn(List<Grupo> grupos);
    List<Jogo> findByEquipeACompeticaoAndGrupoIsNull(Competicao competicao);
}
