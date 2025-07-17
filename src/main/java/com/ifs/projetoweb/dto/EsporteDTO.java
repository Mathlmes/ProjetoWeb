package com.ifs.projetoweb.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EsporteDTO {
    private String nome;
    private Integer minAtletas;
    private Integer maxAtletas;
}
