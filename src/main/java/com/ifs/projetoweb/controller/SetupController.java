package com.ifs.projetoweb.controller;

import com.ifs.projetoweb.service.SetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setup")
public class SetupController {

    @Autowired
    private SetupService setupService;

    @PostMapping("/full-tournament")
    public ResponseEntity<String> setupTournament() {
        String resultado = setupService.setupTournament();
        return ResponseEntity.ok(resultado);
    }
}
