package com.ifs.projetoweb.config;

import com.ifs.projetoweb.entity.*;
import com.ifs.projetoweb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

//@Configuration
public class DataLoader implements CommandLineRunner {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CampusRepository campusRepository;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private EsporteRepository esporteRepository;
    @Autowired private EventoRepository eventoRepository;
    @Autowired private CompeticaoRepository competicaoRepository;
    @Autowired private EquipeRepository equipeRepository;
    @Autowired private GrupoRepository grupoRepository;
    @Autowired private JogoRepository jogoRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // Limpa o banco na ordem correta para evitar erros
        jogoRepository.deleteAll();
        grupoRepository.deleteAll();
        equipeRepository.deleteAll();
        cursoRepository.deleteAll();
        competicaoRepository.deleteAll();
        usuarioRepository.deleteAll();
        eventoRepository.deleteAll();
        esporteRepository.deleteAll();
        campusRepository.deleteAll();

        // --- CRIAÇÃO DOS DADOS DE SUPORTE ---
        Campus campus = campusRepository.save(new Campus(null, "IFS Campus Estância"));
        Esporte esporte = esporteRepository.save(new Esporte(null, "Futsal", 5, 10));
        Evento evento = eventoRepository.save(new Evento(null, "Jogos do Integrado 2025", TipoNivel.INTEGRADO, new ArrayList<>()));
        Usuario coordenador = usuarioRepository.save(new Usuario(null, "Prof. Coordenador", "coord@email.com", "senha123", "111", "Coord", "2025COORD", TipoUsuario.COORDENADOR, new ArrayList<>(), new ArrayList<>()));
        Curso curso = cursoRepository.save(new Curso(null, "Desenvolvimento de Sistemas", TipoNivel.INTEGRADO, campus, coordenador));
        Competicao competicao = competicaoRepository.save(new Competicao(null, evento, esporte));
        List<Usuario> atletasParaSalvar = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            atletasParaSalvar.add(new Usuario(null, "Atleta " + i, "atleta" + i + "@email.com", "123", "tel" + i, "Apelido" + i, "MAT" + i, TipoUsuario.ATLETA, new ArrayList<>(), new ArrayList<>()));
        }
        List<Usuario> atletasSalvos = usuarioRepository.saveAll(atletasParaSalvar);
        List<Equipe> equipesParaSalvar = new ArrayList<>();
        equipesParaSalvar.add(new Equipe(null, "Time Alfa", curso, atletasSalvos.get(0), List.of(atletasSalvos.get(0), atletasSalvos.get(1), atletasSalvos.get(2), atletasSalvos.get(3), atletasSalvos.get(4)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Beta", curso, atletasSalvos.get(5), List.of(atletasSalvos.get(5), atletasSalvos.get(6), atletasSalvos.get(7), atletasSalvos.get(8), atletasSalvos.get(9)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Gama", curso, atletasSalvos.get(10), List.of(atletasSalvos.get(10), atletasSalvos.get(11), atletasSalvos.get(12), atletasSalvos.get(13), atletasSalvos.get(14)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Delta", curso, atletasSalvos.get(1), List.of(atletasSalvos.get(1), atletasSalvos.get(5), atletasSalvos.get(10), atletasSalvos.get(3), atletasSalvos.get(8)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Epsilon", curso, atletasSalvos.get(2), List.of(atletasSalvos.get(2), atletasSalvos.get(6), atletasSalvos.get(11), atletasSalvos.get(4), atletasSalvos.get(7)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Zeta", curso, atletasSalvos.get(3), List.of(atletasSalvos.get(3), atletasSalvos.get(7), atletasSalvos.get(12), atletasSalvos.get(0), atletasSalvos.get(6)), new ArrayList<>(), competicao));
        equipesParaSalvar.add(new Equipe(null, "Time Ômega", curso, atletasSalvos.get(4), List.of(atletasSalvos.get(4), atletasSalvos.get(8), atletasSalvos.get(13), atletasSalvos.get(1), atletasSalvos.get(5)), new ArrayList<>(), competicao));
        equipeRepository.saveAll(equipesParaSalvar);

        System.out.println("\n=====================================================");
        System.out.println("DADOS DE TESTE CARREGADOS COM SUCESSO!");
        System.out.println("Competição de teste criada com ID: " + competicao.getId());
        System.out.println("=====================================================");
    }
}
