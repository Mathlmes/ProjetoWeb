package com.ifs.projetoweb.controller;


import com.ifs.projetoweb.entity.Grupo;
import com.ifs.projetoweb.service.GrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/competicoes/{competicaoId}/grupos")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @PostMapping("/gerar")
    public ResponseEntity<List<Grupo>> gerarGrupos(@PathVariable Long competicaoId) {
        List<Grupo> gruposGerados = grupoService.gerarGrupos(competicaoId);
        return ResponseEntity.ok(gruposGerados);
    }

    @GetMapping
    public ResponseEntity<List<Grupo>> getGruposDaCompeticao(@PathVariable Long competicaoId) {
        List<Grupo> grupos = grupoService.getGruposByCompeticao(competicaoId);
        return ResponseEntity.ok(grupos);
    }
}
