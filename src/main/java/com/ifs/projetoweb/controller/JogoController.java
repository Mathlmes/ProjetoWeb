package com.ifs.projetoweb.controller;

import com.ifs.projetoweb.dto.FaseEliminatoriaDTO;
import com.ifs.projetoweb.dto.PlacarDTO;
import com.ifs.projetoweb.entity.Jogo;
import com.ifs.projetoweb.service.JogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/jogos")
public class JogoController {

    @Autowired
    private JogoService jogoService;

    // Endpoint para lançar o resultado de um jogo específico
    @PatchMapping("/{jogoId}/resultado")
    public ResponseEntity<Jogo> lancarResultado(@PathVariable Long jogoId, @RequestBody PlacarDTO dto) {
        Jogo jogoAtualizado = jogoService.lancarResultado(jogoId, dto);
        return ResponseEntity.ok(jogoAtualizado);
    }

    // Endpoint para gerar a próxima fase de uma competição
    @PostMapping("/competicoes/{competicaoId}/gerar-fase-eliminatoria")
    public ResponseEntity<FaseEliminatoriaDTO> gerarFaseEliminatoria(@PathVariable Long competicaoId) {
        FaseEliminatoriaDTO resultado = jogoService.gerarFaseEliminatoria(competicaoId);
        return ResponseEntity.ok(resultado);
    }
    @GetMapping("/competicao/{competicaoId}/eliminatorias")
    public ResponseEntity<List<Jogo>> getJogosEliminatorios(@PathVariable Long competicaoId) {
        List<Jogo> jogos = jogoService.getJogosEliminatorios(competicaoId);
        return ResponseEntity.ok(jogos);
    }
    }


