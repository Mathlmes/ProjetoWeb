package com.ifs.projetoweb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCompleto;
    private String email;
    private String senha;
    private String telefone;
    private String apelido;
    private String matricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoUsuario tipoUsuario;

    //RELACIONAMENTOS

    // Um usuario pode ser técnico de muitas equipes
    @JsonIgnore // Evita loops infinitos no JSON
    @OneToMany(mappedBy = "tecnico")
    private List<Equipe> equipesComoTecnico = new ArrayList<>();

    // Um usuario pode ser atleta em muitas equipes (relaçao inversa)
    @JsonIgnore // Evita loops infinitos no JSON
    @ManyToMany(mappedBy = "atletas")
    private List<Equipe> equipesComoAtleta = new ArrayList<>();
}
