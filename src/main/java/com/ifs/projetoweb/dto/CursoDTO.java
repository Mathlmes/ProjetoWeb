package com.ifs.projetoweb.dto;


import com.ifs.projetoweb.entity.TipoNivel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CursoDTO {
    private String nome;
    private TipoNivel tipoNivel;
    private Long campusId;
    private Long coordenadorId;
}
