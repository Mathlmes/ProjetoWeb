// src/main/java/com/ifs/projetoweb/dto/FaseEliminatoriaDTO.java
package com.ifs.projetoweb.dto;

import com.ifs.projetoweb.entity.Equipe;
import com.ifs.projetoweb.entity.Jogo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FaseEliminatoriaDTO {
    private List<Jogo> novosJogos;
    private List<Equipe> equipesComBye;
}