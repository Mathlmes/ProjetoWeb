package com.ifs.projetoweb.service;

import com.ifs.projetoweb.dto.CompeticaoDTO;
import com.ifs.projetoweb.entity.Competicao;
import com.ifs.projetoweb.entity.Esporte;
import com.ifs.projetoweb.entity.Evento;
import com.ifs.projetoweb.repository.CompeticaoRepository;
import com.ifs.projetoweb.repository.EsporteRepository;
import com.ifs.projetoweb.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompeticaoService {

    @Autowired
    private CompeticaoRepository competicaoRepository;
    @Autowired
    private EventoRepository eventoRepository;
    @Autowired
    private EsporteRepository esporteRepository;

    public Competicao create(CompeticaoDTO dto) {
        Evento evento = eventoRepository.findById(dto.getEventoId())
                .orElseThrow(() -> new RuntimeException("Evento não encontrado!"));
        Esporte esporte = esporteRepository.findById(dto.getEsporteId())
                .orElseThrow(() -> new RuntimeException("Esporte não encontrado!"));

        Competicao competicao = new Competicao();
        competicao.setEvento(evento);
        competicao.setEsporte(esporte);

        return competicaoRepository.save(competicao);
    }

    public List<Competicao> findAll() {
        return competicaoRepository.findAll();
    }
}