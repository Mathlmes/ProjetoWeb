package com.ifs.projetoweb.service;


import com.ifs.projetoweb.dto.EsporteDTO;
import com.ifs.projetoweb.entity.Esporte;
import com.ifs.projetoweb.repository.EsporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EsporteService {

    @Autowired
    private EsporteRepository esporteRepository;

    public Esporte create (EsporteDTO dto){
        Esporte esporte = new Esporte();
        esporte.setNome(dto.getNome());
        esporte.setMaxAtletas(dto.getMaxAtletas());
        esporte.setMinAtletas(dto.getMinAtletas());
        return esporteRepository.save(esporte);
    }
}
