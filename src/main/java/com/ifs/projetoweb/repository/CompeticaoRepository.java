package com.ifs.projetoweb.repository;


import com.ifs.projetoweb.entity.Competicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompeticaoRepository extends JpaRepository<Competicao, Long> {

}
