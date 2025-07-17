package com.ifs.projetoweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlacarDTO {
    private int placarEquipeA;
    private int placarEquipeB;
    private boolean houveWO; // Para o árbitro poder informar um W.O.
}
