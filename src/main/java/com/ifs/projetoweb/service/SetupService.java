package com.ifs.projetoweb.service;

import com.ifs.projetoweb.entity.*;
import com.ifs.projetoweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        // Alterado para Ping-Pong com as novas regras
        Esporte esporte = esporteRepository.save(new Esporte(null, "Ping-Pong", 1, 2));
        Evento evento = eventoRepository.save(new Evento(null, "Torneio de Ping-Pong 2025", TipoNivel.INTEGRADO, new ArrayList<>()));
        Usuario coordenador = usuarioRepository.save(new Usuario(null, "Prof. Coordenador", "coord@email.com", "senha123", "111", "Coord", "2025COORD", TipoUsuario.COORDENADOR, new ArrayList<>(), new ArrayList<>()));
        Usuario arbitro = usuarioRepository.save(new Usuario(null, "Juiz Oficial", "arbitro@email.com", "senha123", "222", "Arbitro", "2025ARBITRO", TipoUsuario.ARBITRO, new ArrayList<>(), new ArrayList<>()));
        System.out.println("Árbitro de teste criado com matrícula: " + arbitro.getMatricula());

        List<Curso> cursos = criarNoveCursos(campus, coordenador);

        Competicao competicao = competicaoRepository.save(new Competicao(null, evento, esporte));


        System.out.println("Criando 18 atletas e 9 equipes de Ping-Pong...");
        // Criando o número exato de atletas necessários (9 equipes * 2 atletas)
        List<Usuario> atletasSalvos = criarAtletas(18);
        criarNoveEquipesDePingPong(cursos, competicao, atletasSalvos);
        System.out.println("Dados de teste (9 equipes com cursos e atletas únicos) criados com sucesso.");

        return "Banco de dados resetado e populado com 9 equipes de Ping-Pong. Competição de teste criada com ID: " + competicao.getId();
    }

    private List<Usuario> criarAtletas(int quantidade) {
        List<Usuario> atletasParaSalvar = new ArrayList<>();
        for (int i = 1; i <= quantidade; i++) {
            atletasParaSalvar.add(new Usuario(null, "Atleta " + i, "atleta" + i + "@email.com", "123", "tel" + i, "Apelido" + i, "IFS" + i, TipoUsuario.ATLETA, new ArrayList<>(), new ArrayList<>()));
        }
        return usuarioRepository.saveAll(atletasParaSalvar);
    }


    private List<Curso> criarNoveCursos(Campus campus, Usuario coordenador) {
        List<Curso> cursosParaSalvar = new ArrayList<>();
        String[] nomesCursos = {
                "Desenvolvimento de Sistemas", "Eletrotécnica", "Edificações", "Química",
                "Automação Industrial", "Petróleo e Gás", "Segurança do Trabalho",
                "Alimentos", "Guia de Turismo"
        };
        for (String nome : nomesCursos) {
            cursosParaSalvar.add(new Curso(null, nome, TipoNivel.INTEGRADO, campus, coordenador));
        }
        return cursoRepository.saveAll(cursosParaSalvar);
    }


    private void criarNoveEquipesDePingPong(List<Curso> cursos, Competicao competicao, List<Usuario> atletas) {
        if (atletas.size() < 18 || cursos.size() < 9) {
            throw new RuntimeException("São necessários pelo menos 18 atletas e 9 cursos para criar as 9 equipes de teste.");
        }

        List<Equipe> equipesParaSalvar = new ArrayList<>();
        String[] nomesEquipes = {"Dupla Alfa", "Dupla Beta", "Dupla Gama", "Dupla Delta", "Dupla Epsilon", "Dupla Zeta", "Dupla Ômega", "Dupla Phoenix", "Dupla Hydra"};

        int atletaIndex = 0;
        for (int i = 0; i < 9; i++) {
            // Cada equipe recebe um curso diferente
            Curso cursoDaEquipe = cursos.get(i);

            // Pega o técnico (o primeiro atleta da dupla)
            Usuario tecnico = atletas.get(atletaIndex);

            // Cria a lista de 2 atletas para esta equipe (a dupla)
            List<Usuario> atletasDaEquipe = new ArrayList<>();
            atletasDaEquipe.add(atletas.get(atletaIndex++));
            atletasDaEquipe.add(atletas.get(atletaIndex++));

            // Adiciona a nova equipe à lista para salvar
            equipesParaSalvar.add(new Equipe(null, nomesEquipes[i], cursoDaEquipe, tecnico, atletasDaEquipe, new ArrayList<>(), competicao));
        }

        equipeRepository.saveAll(equipesParaSalvar);
    }
}