package com.ifs.projetoweb.service;

import com.ifs.projetoweb.entity.*;
import com.ifs.projetoweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SetupService {

    // Injetando todos os repositórios que vamos precisar
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CampusRepository campusRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private EsporteRepository esporteRepository;
    @Autowired private EventoRepository eventoRepository;
    @Autowired private CompeticaoRepository competicaoRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private GrupoRepository grupoRepository;
    @Autowired private JogoRepository jogoRepository;

    @Transactional
    public String setupTournament() {
        // 1. LIMPA O BANCO NA ORDEM CORRETA
        System.out.println("Limpando o banco de dados...");
        jogoRepository.deleteAll();
        grupoRepository.deleteAll();
        equipeRepository.deleteAll();
        cursoRepository.deleteAll();
        competicaoRepository.deleteAll();
        usuarioRepository.deleteAll();
        eventoRepository.deleteAll();
        esporteRepository.deleteAll();
        campusRepository.deleteAll();
        System.out.println("Banco de dados limpo.");

        // 2. CRIA OS DADOS DE SUPORTE
        System.out.println("Criando dados de suporte...");
        Campus campus = campusRepository.save(new Campus(null, "IFS Campus Estância"));
        Esporte esporte = esporteRepository.save(new Esporte(null, "Futsal", 5, 10));
        Evento evento = eventoRepository.save(new Evento(null, "Jogos do Integrado 2025 - Cenário 9 Equipas", TipoNivel.INTEGRADO, new ArrayList<>()));
        Usuario coordenador = usuarioRepository.save(new Usuario(null, "Prof. Coordenador", "coord@email.com", "senha123", "111", "Coord", "2025COORD", TipoUsuario.COORDENADOR, new ArrayList<>(), new ArrayList<>()));
        Curso curso = cursoRepository.save(new Curso(null, "Desenvolvimento de Sistemas", TipoNivel.INTEGRADO, campus, coordenador));
        Competicao competicao = competicaoRepository.save(new Competicao(null, evento, esporte));

        // 3. CRIA OS ATLETAS E EQUIPES
        System.out.println("Criando atletas e 9 equipes...");
        // Aumentando o número de atletas para garantir que há jogadores suficientes
        List<Usuario> atletasSalvos = criarAtletas(25);
        criarNoveEquipes(curso, competicao, atletasSalvos);
        System.out.println("Dados de teste (9 equipes) criados com sucesso.");

        return "Banco de dados resetado e populado com 9 equipes. Competição de teste criada com ID: " + competicao.getId();
    }

    private List<Usuario> criarAtletas(int quantidade) {
        List<Usuario> atletasParaSalvar = new ArrayList<>();
        for (int i = 1; i <= quantidade; i++) {
            atletasParaSalvar.add(new Usuario(null, "Atleta " + i, "atleta" + i + "@email.com", "123", "tel" + i, "Apelido" + i, "IFS" + i, TipoUsuario.ATLETA, new ArrayList<>(), new ArrayList<>()));
        }
        return usuarioRepository.saveAll(atletasParaSalvar);
    }

    // Método específico para criar 9 equipes distintas
    private void criarNoveEquipes(Curso curso, Competicao competicao, List<Usuario> atletas) {
        if (atletas.size() < 20) {
            throw new RuntimeException("São necessários pelo menos 20 atletas para criar as 9 equipes de teste.");
        }

        List<Equipe> equipesParaSalvar = new ArrayList<>();

        // Equipe 1 a 5 (usando os primeiros atletas)
        equipesParaSalvar.add(new Equipe(null, "Time Alfa", curso, atletas.get(0), List.of(atletas.get(0), atletas.get(1), atletas.get(2), atletas.get(3), atletas.get(4)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Beta", curso, atletas.get(5), List.of(atletas.get(5), atletas.get(6), atletas.get(7), atletas.get(8), atletas.get(9)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Gama", curso, atletas.get(10), List.of(atletas.get(10), atletas.get(11), atletas.get(12), atletas.get(13), atletas.get(14)), new ArrayList<>(), competicao));

        // Equipe 4 a 9 (usando atletas restantes e alguns repetidos para simular a realidade)
        equipesParaSalvar.add(new Equipe(null, "Time Delta", curso, atletas.get(15), List.of(atletas.get(15), atletas.get(16), atletas.get(17), atletas.get(1), atletas.get(3)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Epsilon", curso, atletas.get(18), List.of(atletas.get(18), atletas.get(19), atletas.get(0), atletas.get(5), atletas.get(8)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Zeta", curso, atletas.get(20), List.of(atletas.get(20), atletas.get(21), atletas.get(6), atletas.get(11), atletas.get(13)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Ômega", curso, atletas.get(22), List.of(atletas.get(22), atletas.get(23), atletas.get(4), atletas.get(9), atletas.get(12)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Phoenix", curso, atletas.get(24), List.of(atletas.get(24), atletas.get(2), atletas.get(7), atletas.get(16), atletas.get(19)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Hydra", curso, atletas.get(1), List.of(atletas.get(1), atletas.get(10), atletas.get(15), atletas.get(20), atletas.get(23)), new ArrayList<>(), competicao));

        equipeRepository.saveAll(equipesParaSalvar);
    }
}