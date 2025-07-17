package com.ifs.projetoweb.controller;


import com.ifs.projetoweb.dto.CampusDTO;
import com.ifs.projetoweb.entity.Campus;
import com.ifs.projetoweb.service.CampusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/campus")
public class CampusController {
    @Autowired
    private CampusService campusService;

    @PostMapping
    public ResponseEntity<Campus>create(@RequestBody CampusDTO dto){
        Campus novoCampus = campusService.create(dto);
        return ResponseEntity.ok(novoCampus);
    }
}
