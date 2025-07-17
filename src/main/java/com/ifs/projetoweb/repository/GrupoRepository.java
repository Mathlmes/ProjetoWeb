package com.ifs.projetoweb.repository;

import com.ifs.projetoweb.entity.Competicao;
import com.ifs.projetoweb.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    // NOVO MÉTODO - A SOLUÇÃO
    // Esta consulta força o Hibernate a buscar os grupos E as equipes de cada grupo de uma só vez.
    @Query("SELECT g FROM Grupo g JOIN FETCH g.equipes WHERE g.competicao = :competicao")
    List<Grupo> findByCompeticaoWithEquipes(@Param("competicao") Competicao competicao);


}
