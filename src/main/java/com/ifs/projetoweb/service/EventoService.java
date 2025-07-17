package com.ifs.projetoweb.service;

import com.ifs.projetoweb.dto.EventoDTO;
import com.ifs.projetoweb.entity.Evento;
import com.ifs.projetoweb.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public Evento create(EventoDTO dto) {
        Evento evento = new Evento();
        evento.setNome(dto.getNome());
        evento.setNivel(dto.getNivel());
        return eventoRepository.save(evento);
    }
}