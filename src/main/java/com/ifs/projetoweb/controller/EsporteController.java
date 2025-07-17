package com.ifs.projetoweb.controller;



import com.ifs.projetoweb.dto.EsporteDTO;
import com.ifs.projetoweb.entity.Esporte;
import com.ifs.projetoweb.service.EsporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/esporte")
public class EsporteController {

    @Autowired
    private EsporteService esporteService;

    @PostMapping
    public ResponseEntity<Esporte> create(@RequestBody EsporteDTO dto, UriComponentsBuilder uriBuilder){
        Esporte novoEsporte = esporteService.create(dto);
        URI uri = uriBuilder.path("/esporte/{id}").buildAndExpand(novoEsporte.getId()).toUri();
        return ResponseEntity.created(uri).body(novoEsporte);
    }
}
