package com.ifs.projetoweb.service;


import com.ifs.projetoweb.dto.CampusDTO;
import com.ifs.projetoweb.entity.Campus;
import com.ifs.projetoweb.repository.CampusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

@Service
public class CampusService {

    @Autowired
    private CampusRepository campusRepository;

    public Campus create (CampusDTO dto){
        Campus campus = new Campus();
        campus.setNome(dto.getNome());
        return campusRepository.save(campus);
    }
}
