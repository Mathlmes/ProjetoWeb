package com.ifs.projetoweb.controller;


import com.ifs.projetoweb.dto.UsuarioDTO;
import com.ifs.projetoweb.entity.Usuario;
import com.ifs.projetoweb.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping ("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar/atleta")
    public ResponseEntity<Usuario> registrarAtleta(@RequestBody UsuarioDTO dto, UriComponentsBuilder uriBuilder) {
        Usuario novoAtleta = usuarioService.registrarAtleta(dto);

        URI uri = uriBuilder.path("/usuarios/{id}").build(novoAtleta.getId());

        return ResponseEntity.created(uri).body(novoAtleta);
    }

    @PostMapping("/coordenador")
    public ResponseEntity<Usuario> criarCoordenador(@RequestBody UsuarioDTO dto, UriComponentsBuilder uriBuilder) {
        Usuario novoCoordenador = usuarioService.criarCoordenador(dto);

        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(novoCoordenador.getId()).toUri();

        return ResponseEntity.created(uri).body(novoCoordenador);
    }

    @PostMapping("/arbitro")
    public ResponseEntity<Usuario> criarArbitro(@RequestBody UsuarioDTO dto, UriComponentsBuilder uriBuilder) {
        Usuario novoArbitro = usuarioService.criarArbitro(dto);

        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(novoArbitro.getId()).toUri();

        return ResponseEntity.created(uri).body(novoArbitro);
    }

    @CrossOrigin(origins = "*") // Permissão para o Flutter acessar
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Usuario> getUsuarioPorMatricula(@PathVariable String matricula) {
        Usuario usuario = usuarioService.findByMatricula(matricula);
        return ResponseEntity.ok(usuario);
    }
}
