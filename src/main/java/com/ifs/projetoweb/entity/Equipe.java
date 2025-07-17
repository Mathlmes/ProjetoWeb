package com.ifs.projetoweb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_equipe")
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Usuario tecnico;

    @ManyToMany
    @JoinTable(
            name = "tb_equipe_atleta",
            joinColumns = @JoinColumn(name = "equipe_id"),
            inverseJoinColumns = @JoinColumn(name = "atleta_id")
    )
    private List<Usuario> atletas = new ArrayList<>();


    // "mappedBy" diz ao JPA para procurar a configuração no campo "equipes" da classe Grupo.
    @JsonBackReference
    @ManyToMany(mappedBy = "equipes")
    private List<Grupo> grupos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "competicao_id", nullable = false)
    private Competicao competicao;
}