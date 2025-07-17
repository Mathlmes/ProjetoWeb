package com.ifs.projetoweb.repository;


import com.ifs.projetoweb.entity.Esporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsporteRepository extends JpaRepository<Esporte, Long>{
}
