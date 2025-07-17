package com.ifs.projetoweb.service;

import com.ifs.projetoweb.dto.PlacarDTO;
import com.ifs.projetoweb.entity.*;
import com.ifs.projetoweb.repository.CompeticaoRepository;
import com.ifs.projetoweb.repository.GrupoRepository;
import com.ifs.projetoweb.repository.JogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JogoService {

    @Autowired
    private JogoRepository jogoRepository;
    @Autowired
    private CompeticaoRepository competicaoRepository;
    @Autowired
    private GrupoRepository grupoRepository;

    @Transactional
    public Jogo lancarResultado(Long jogoId, PlacarDTO dto) {
        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado!"));

        jogo.setPlacarEquipeA(dto.getPlacarEquipeA());
        jogo.setPlacarEquipeB(dto.getPlacarEquipeB());
        jogo.setHouveWO(dto.isHouveWO());

        if (dto.getPlacarEquipeA() > dto.getPlacarEquipeB()) {
            jogo.setVencedor(jogo.getEquipeA());
        } else if (dto.getPlacarEquipeB() > dto.getPlacarEquipeA()) {
            jogo.setVencedor(jogo.getEquipeB());
        } else {
            jogo.setVencedor(null);
        }
        return jogoRepository.save(jogo);
    }

    @Transactional
    public List<Jogo> gerarFaseEliminatoria(Long competicaoId) {
        Competicao competicao = competicaoRepository.findById(competicaoId)
                .orElseThrow(() -> new RuntimeException("Competição não encontrada!"));

        List<Grupo> grupos = grupoRepository.findByCompeticaoWithEquipes(competicao);
        if (grupos.isEmpty()) {
            throw new RuntimeException("Não há grupos gerados para esta competição.");
        }

        validarFimDaFaseDeGrupos(grupos);

        Map<Long, Grupo> mapaOrigemEquipe = new HashMap<>();
        List<Equipe> todosOsClassificados = new ArrayList<>();

        for (Grupo grupo : grupos) {
            List<Equipe> classificadosDoGrupo = calcularClassificacao(grupo);
            for (Equipe equipe : classificadosDoGrupo) {
                mapaOrigemEquipe.put(equipe.getId(), grupo);
                todosOsClassificados.add(equipe);
            }
        }

        return criarJogosEliminatorios(todosOsClassificados, grupos, mapaOrigemEquipe);
    }

    // --- LÓGICA DE CRIAÇÃO DE JOGOS COM CORREÇÃO DE BUG ---
    private List<Jogo> criarJogosEliminatorios(List<Equipe> classificados, List<Grupo> gruposDaCompeticao, Map<Long, Grupo> mapaOrigemEquipe) {
        int numeroDeEquipes = classificados.size();
        if (numeroDeEquipes < 2) return new ArrayList<>();

        int tamanhoDaChave = 1;
        while (tamanhoDaChave < numeroDeEquipes) {
            tamanhoDaChave *= 2;
        }

        int numeroDeByes = tamanhoDaChave - numeroDeEquipes;


        List<EstatisticasEquipe> rankingGeral = rankTeams(classificados, gruposDaCompeticao);

        List<Equipe> equipesComBye = new ArrayList<>();
        for (int i = 0; i < numeroDeByes; i++) {
            equipesComBye.add(rankingGeral.get(i).getEquipe());
        }
        equipesComBye.forEach(e -> System.out.println("Equipe com Bye (avança direto): " + e.getNome()));

        List<Equipe> equipesQueJogam = new ArrayList<>();
        for (int i = numeroDeByes; i < rankingGeral.size(); i++) {
            equipesQueJogam.add(rankingGeral.get(i).getEquipe());
        }

        List<Jogo> novosJogos = new ArrayList<>();
        Etapa etapaAtual = definirEtapa(tamanhoDaChave);

        List<Equipe> adversariosDisponiveis = new LinkedList<>(equipesQueJogam);
        while (adversariosDisponiveis.size() >= 2) {
            Equipe equipeA = adversariosDisponiveis.remove(0);
            Equipe equipeB = encontrarAdversarioValido(equipeA, adversariosDisponiveis, mapaOrigemEquipe);
            adversariosDisponiveis.remove(equipeB);

            novosJogos.add(criarJogo(equipeA, equipeB, etapaAtual));
        }

        return novosJogos;
    }

    private Equipe encontrarAdversarioValido(Equipe equipePrincipal, List<Equipe> listaAdversarios, Map<Long, Grupo> mapaOrigem) {
        Grupo grupoOrigem = mapaOrigem.get(equipePrincipal.getId());
        for (int i = listaAdversarios.size() - 1; i >= 0; i--) {
            Equipe adversarioPotencial = listaAdversarios.get(i);
            if (!mapaOrigem.get(adversarioPotencial.getId()).equals(grupoOrigem)) {
                return adversarioPotencial;
            }
        }
        return listaAdversarios.get(listaAdversarios.size() - 1);
    }

    // --- MÉTODO DE RANKING CORRIGIDO ---
    private List<EstatisticasEquipe> rankTeams(List<Equipe> equipes, List<Grupo> grupos) {
        Map<Long, EstatisticasEquipe> estatisticasMap = new HashMap<>();
        // Inicia o mapa apenas com as equipes que nos interessam (as classificadas)
        for (Equipe equipe : equipes) {
            estatisticasMap.put(equipe.getId(), new EstatisticasEquipe(equipe));
        }

        // Busca TODOS os jogos da fase de grupos
        List<Jogo> todosOsJogosDosGrupos = jogoRepository.findByGrupoIn(grupos);

        // Itera por todos os jogos para calcular as estatísticas
        for (Jogo jogo : todosOsJogosDosGrupos) {
            Equipe equipeA = jogo.getEquipeA();
            Equipe equipeB = jogo.getEquipeB();

            // Pega as estatísticas das equipes do jogo ATUAL
            EstatisticasEquipe statsA = estatisticasMap.get(equipeA.getId());
            EstatisticasEquipe statsB = estatisticasMap.get(equipeB.getId());

            // CORREÇÃO: A lógica agora atualiza as estatísticas se a equipe estiver no mapa,
            // independentemente de seu oponente estar.
            if (statsA != null) {
                statsA.golsPro += jogo.getPlacarEquipeA();
                statsA.golsContra += jogo.getPlacarEquipeB();
                if (jogo.getVencedor() == null) {
                    statsA.pontos += 1;
                } else if (jogo.getVencedor().getId().equals(equipeA.getId())) {
                    statsA.pontos += 3;
                }
            }

            if (statsB != null) {
                statsB.golsPro += jogo.getPlacarEquipeB();
                statsB.golsContra += jogo.getPlacarEquipeA();
                if (jogo.getVencedor() == null) {
                    statsB.pontos += 1;
                } else if (jogo.getVencedor().getId().equals(equipeB.getId())) {
                    statsB.pontos += 3;
                }
            }
        }

        // Retorna a lista de estatísticas, ordenada pelos critérios corretos.
        return estatisticasMap.values().stream()
                .sorted(Comparator.comparingInt(EstatisticasEquipe::getPontos).reversed()
                        .thenComparingInt(EstatisticasEquipe::getSaldoDeGols).reversed()
                        .thenComparingInt(EstatisticasEquipe::getGolsPro).reversed())
                .collect(Collectors.toList());
    }

    // --- DEMAIS MÉTODOS AUXILIARES (SEM ALTERAÇÕES) ---
    private Etapa definirEtapa(int tamanhoDaChave) {
        switch (tamanhoDaChave) {
            case 2: return Etapa.FINAL;
            case 4: return Etapa.SEMIFINAL;
            case 8: return Etapa.QUARTAS_DE_FINAL;
            default: return Etapa.QUARTAS_DE_FINAL;
        }
    }

    private Jogo criarJogo(Equipe equipeA, Equipe equipeB, Etapa etapa) {
        Jogo jogo = new Jogo();
        jogo.setEquipeA(equipeA);
        jogo.setEquipeB(equipeB);
        jogo.setEtapa(etapa);
        jogo.setDataHora(LocalDateTime.now());
        return jogoRepository.save(jogo);
    }

    private void validarFimDaFaseDeGrupos(List<Grupo> grupos) {
        List<Jogo> todosOsJogos = jogoRepository.findByGrupoIn(grupos);
        for (Jogo jogo : todosOsJogos) {
            if (jogo.getPlacarEquipeA() == null || jogo.getPlacarEquipeB() == null) {
                throw new RuntimeException("Não é possível gerar a fase eliminatória. O jogo ID " + jogo.getId() + " ainda não tem um resultado.");
            }
        }
    }

    private List<Equipe> calcularClassificacao(Grupo grupo) {
        Map<Long, EstatisticasEquipe> estatisticas = new HashMap<>();
        grupo.getEquipes().forEach(equipe -> estatisticas.put(equipe.getId(), new EstatisticasEquipe(equipe)));
        List<Jogo> jogosDoGrupo = jogoRepository.findByGrupo(grupo);

        for (Jogo jogo : jogosDoGrupo) {
            EstatisticasEquipe statsA = estatisticas.get(jogo.getEquipeA().getId());
            EstatisticasEquipe statsB = estatisticas.get(jogo.getEquipeB().getId());
            statsA.golsPro += jogo.getPlacarEquipeA();
            statsA.golsContra += jogo.getPlacarEquipeB();
            statsB.golsPro += jogo.getPlacarEquipeB();
            statsB.golsContra += jogo.getPlacarEquipeA();
            if (jogo.getVencedor() == null) {
                statsA.pontos += 1;
                statsB.pontos += 1;
            } else if (jogo.getVencedor().getId().equals(statsA.equipe.getId())) {
                statsA.pontos += 3;
            } else {
                statsB.pontos += 3;
            }
        }
        return estatisticas.values().stream()
                .sorted(Comparator.comparingInt(EstatisticasEquipe::getPontos).reversed()
                        .thenComparingInt(EstatisticasEquipe::getSaldoDeGols).reversed()
                        .thenComparingInt(EstatisticasEquipe::getGolsPro).reversed())
                .limit(2)
                .map(EstatisticasEquipe::getEquipe)
                .collect(Collectors.toList());
    }

    private static class EstatisticasEquipe {
        Equipe equipe;
        int pontos = 0;
        int golsPro = 0;
        int golsContra = 0;
        EstatisticasEquipe(Equipe equipe) { this.equipe = equipe; }
        int getPontos() { return pontos; }
        int getGolsPro() { return golsPro; }
        int getSaldoDeGols() { return golsPro - golsContra; }
        Equipe getEquipe() { return equipe; }
    }
}