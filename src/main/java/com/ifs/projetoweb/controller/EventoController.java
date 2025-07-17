package com.ifs.projetoweb.controller;

import com.ifs.projetoweb.dto.EventoDTO;
import com.ifs.projetoweb.entity.Evento;
import com.ifs.projetoweb.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @PostMapping
    public ResponseEntity<Evento> create(@RequestBody EventoDTO dto, UriComponentsBuilder uriBuilder) {
        Evento novoEvento = eventoService.create(dto);
        URI uri = uriBuilder.path("/eventos/{id}").buildAndExpand(novoEvento.getId()).toUri();
        return ResponseEntity.created(uri).body(novoEvento);
    }
}