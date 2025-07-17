package com.ifs.projetoweb.controller;

import com.ifs.projetoweb.dto.CursoDTO;
import com.ifs.projetoweb.entity.Curso;
import com.ifs.projetoweb.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    @PostMapping
    public ResponseEntity<Curso> create(@RequestBody CursoDTO dto, UriComponentsBuilder uriBuilder) {
        Curso novoCurso = cursoService.create(dto);
        URI uri = uriBuilder.path("/cursos/{id}").buildAndExpand(novoCurso.getId()).toUri();
        return ResponseEntity.created(uri).body(novoCurso);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cursoService.delete(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content (sucesso sem corpo)
    }
}