package com.ifs.projetoweb.controller;

import com.ifs.projetoweb.dto.CompeticaoDTO;
import com.ifs.projetoweb.entity.Competicao;
import com.ifs.projetoweb.service.CompeticaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/competicoes")
public class CompeticaoController {

    @Autowired
    private CompeticaoService competicaoService;

    @PostMapping
    public ResponseEntity<Competicao> create(@RequestBody CompeticaoDTO dto, UriComponentsBuilder uriBuilder) {
        Competicao novaCompeticao = competicaoService.create(dto);
        URI uri = uriBuilder.path("/competicoes/{id}").buildAndExpand(novaCompeticao.getId()).toUri();
        return ResponseEntity.created(uri).body(novaCompeticao);
    }

    @GetMapping
    public ResponseEntity<List<Competicao>> findAll() {
        List<Competicao> lista = competicaoService.findAll();
        return ResponseEntity.ok(lista);
    }
}