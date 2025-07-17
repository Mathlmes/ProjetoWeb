package com.ifs.projetoweb.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private String nomeCompleto;
    private String email;
    private String senha;
    private String telefone;
    private String apelido;
    private String matricula;
}
