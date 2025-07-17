package com.ifs.projetoweb.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EquipeCreateDTO {

    private String nome;
    private Long cursoId;
    private Long competicaoId;
    private Long tecnicoId;
    private List<Long> atletaIds;

}
