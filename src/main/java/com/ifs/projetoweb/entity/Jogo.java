package com.ifs.projetoweb.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_jogo")
public class Jogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Etapa etapa;
    private Integer placarEquipeA;
    private Integer placarEquipeB;
    private boolean houveWO;
    @ManyToOne
    @JoinColumn(name = "equipe_a_id", nullable = false)
    private Equipe equipeA;

    @ManyToOne
    @JoinColumn(name = "equipe_b_id", nullable = false)
    private Equipe equipeB;

    @ManyToOne
    @JoinColumn(name = "arbitro_id")
    private Usuario arbitro;

    @ManyToOne
    @JoinColumn(name = "vencedor_id") // Pode ser nulo antes do fim do jogo
    private Equipe vencedor;

    @ManyToOne
    @JoinColumn(name = "grupo_id") // Pode ser nulo para jogos de mata-mata
    private Grupo grupo;
}
