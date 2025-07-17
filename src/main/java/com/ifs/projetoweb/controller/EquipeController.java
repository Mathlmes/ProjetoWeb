package com.ifs.projetoweb.controller;

import com.ifs.projetoweb.dto.EquipeCreateDTO;
import com.ifs.projetoweb.entity.Equipe;
import com.ifs.projetoweb.service.EquipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/equipes")
public class EquipeController {

    @Autowired
    private EquipeService equipeService;

    @PostMapping
    public ResponseEntity<Equipe> create(@RequestBody EquipeCreateDTO dto, UriComponentsBuilder uriBuilder) {

        Equipe novaEquipe = equipeService.create(dto);

        // retorna a resposta correta (201 Created)
        URI uri = uriBuilder.path("/equipes/{id}").buildAndExpand(novaEquipe.getId()).toUri();
        return ResponseEntity.created(uri).body(novaEquipe);
    }
}
