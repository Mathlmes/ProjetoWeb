// Substitua o conteúdo inteiro do seu JogoService.java por este código melhorado
package com.ifs.projetoweb.service;

import com.ifs.projetoweb.dto.FaseEliminatoriaDTO;
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

        if (dto.getEquipeIdPerdedoraWO() != null) {
            jogo.setHouveWO(true);
            if (dto.getEquipeIdPerdedoraWO().equals(jogo.getEquipeA().getId())) {
                jogo.setVencedor(jogo.getEquipeB());
                jogo.setPlacarEquipeA(0);
                jogo.setPlacarEquipeB(1);
            } else {
                jogo.setVencedor(jogo.getEquipeA());
                jogo.setPlacarEquipeA(1);
                jogo.setPlacarEquipeB(0);
            }
        } else {
            jogo.setHouveWO(false);
            jogo.setPlacarEquipeA(dto.getPlacarEquipeA());
            jogo.setPlacarEquipeB(dto.getPlacarEquipeB());

            if (dto.getPlacarEquipeA() > dto.getPlacarEquipeB()) {
                jogo.setVencedor(jogo.getEquipeA());
            } else if (dto.getPlacarEquipeB() > dto.getPlacarEquipeA()) {
                jogo.setVencedor(jogo.getEquipeB());
            } else {
                jogo.setVencedor(null);
            }
        }
        return jogoRepository.save(jogo);
    }

    @Transactional
    public FaseEliminatoriaDTO gerarFaseEliminatoria(Long competicaoId) {
        System.out.println("\n--- INICIANDO GERAÇÃO DA FASE ELIMINATÓRIA ---");
        Competicao competicao = competicaoRepository.findById(competicaoId)
                .orElseThrow(() -> new RuntimeException("Competição não encontrada!"));

        List<Grupo> grupos = grupoRepository.findByCompeticaoWithEquipes(competicao);
        if (grupos.isEmpty()) {
            throw new RuntimeException("Não há grupos gerados para esta competição.");
        }

        validarFimDaFaseDeGrupos(grupos);

        Map<Long, Grupo> mapaOrigemEquipe = new HashMap<>();
        List<Equipe> todosOsClassificados = new ArrayList<>();

        System.out.println("Calculando classificados de cada grupo...");
        for (Grupo grupo : grupos) {
            List<Equipe> classificadosDoGrupo = calcularClassificacao(grupo);
            System.out.println(" > Grupo '" + grupo.getNome() + "': Classificados IDs -> " +
                    classificadosDoGrupo.stream().map(Equipe::getId).collect(Collectors.toList()));

            for (Equipe equipe : classificadosDoGrupo) {
                mapaOrigemEquipe.put(equipe.getId(), grupo);
                todosOsClassificados.add(equipe);
            }
        }
        System.out.println("Total de classificados: " + todosOsClassificados.size());

        return criarJogosEliminatorios(todosOsClassificados, grupos, mapaOrigemEquipe);
    }

    @Transactional(readOnly = true)
    public List<Jogo> getJogosEliminatorios(Long competicaoId) {
        Competicao competicao = competicaoRepository.findById(competicaoId)
                .orElseThrow(() -> new RuntimeException("Competição não encontrada!"));
        return jogoRepository.findByEquipeACompeticaoAndGrupoIsNull(competicao);
    }

    private FaseEliminatoriaDTO criarJogosEliminatorios(List<Equipe> classificados, List<Grupo> gruposDaCompeticao, Map<Long, Grupo> mapaOrigemEquipe) {
        int numeroDeEquipes = classificados.size();
        if (numeroDeEquipes < 2) {
            return new FaseEliminatoriaDTO(new ArrayList<>(), new ArrayList<>());
        }

        int tamanhoDaChave = 1;
        while (tamanhoDaChave < numeroDeEquipes) {
            tamanhoDaChave *= 2;
        }
        int numeroDeByes = tamanhoDaChave - numeroDeEquipes;
        System.out.println("Número de equipes na fase: " + numeroDeEquipes + ". Número de Byes: " + numeroDeByes);

        List<EstatisticasEquipe> rankingGeral = rankTeams(classificados, gruposDaCompeticao);
        System.out.println("Ranking geral dos classificados:");
        rankingGeral.forEach(e -> System.out.println("  -> ID: " + e.getEquipe().getId() + ", Pontos: " + e.getPontos() + ", SG: " + e.getSaldoDeGols()));

        List<Equipe> equipesComBye = rankingGeral.stream()
                .limit(numeroDeByes)
                .map(EstatisticasEquipe::getEquipe)
                .collect(Collectors.toList());
        System.out.println("Equipes com Bye (IDs): " + equipesComBye.stream().map(Equipe::getId).collect(Collectors.toList()));

        List<Equipe> equipesQueJogam = rankingGeral.stream()
                .skip(numeroDeByes)
                .map(EstatisticasEquipe::getEquipe)
                .collect(Collectors.toList());
        System.out.println("Equipes que jogam (IDs): " + equipesQueJogam.stream().map(Equipe::getId).collect(Collectors.toList()));

        List<Jogo> novosJogos = new ArrayList<>();
        Etapa etapaAtual = definirEtapa(tamanhoDaChave);

        List<Equipe> adversariosDisponiveis = new LinkedList<>(equipesQueJogam);
        while (adversariosDisponiveis.size() >= 2) {
            Equipe equipeA = adversariosDisponiveis.remove(0);
            Equipe equipeB = encontrarAdversarioValido(equipeA, adversariosDisponiveis, mapaOrigemEquipe);
            adversariosDisponiveis.remove(equipeB);
            novosJogos.add(criarJogo(equipeA, equipeB, etapaAtual));
        }

        System.out.println("--- GERAÇÃO DA FASE ELIMINATÓRIA CONCLUÍDA ---\n");
        return new FaseEliminatoriaDTO(jogoRepository.saveAll(novosJogos), equipesComBye);
    }

    private List<Equipe> calcularClassificacao(Grupo grupo) {
        Map<Long, EstatisticasEquipe> estatisticas = new HashMap<>();
        grupo.getEquipes().forEach(equipe -> estatisticas.put(equipe.getId(), new EstatisticasEquipe(equipe)));

        List<Jogo> jogosDoGrupo = jogoRepository.findByGrupo(grupo);

        for (Jogo jogo : jogosDoGrupo) {
            EstatisticasEquipe statsA = estatisticas.get(jogo.getEquipeA().getId());
            EstatisticasEquipe statsB = estatisticas.get(jogo.getEquipeB().getId());

            Integer placarA = jogo.getPlacarEquipeA();
            Integer placarB = jogo.getPlacarEquipeB();

            if (placarA != null && placarB != null) {
                statsA.golsPro += placarA;
                statsA.golsContra += placarB;
                statsB.golsPro += placarB;
                statsB.golsContra += placarA;

                if (jogo.getVencedor() == null) {
                    statsA.pontos += 1;
                    statsB.pontos += 1;
                } else if (jogo.getVencedor().getId().equals(statsA.equipe.getId())) {
                    statsA.pontos += 3;
                } else {
                    statsB.pontos += 3;
                }
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

    // --- DEMAIS MÉTODOS AUXILIARES (sem alterações) ---

    private Equipe encontrarAdversarioValido(Equipe equipePrincipal, List<Equipe> listaAdversarios, Map<Long, Grupo> mapaOrigem) {
        Grupo grupoOrigem = mapaOrigem.get(equipePrincipal.getId());
        for (int i = listaAdversarios.size() - 1; i >= 0; i--) {
            Equipe adversarioPotencial = listaAdversarios.get(i);
            if (!mapaOrigem.get(adversarioPotencial.getId()).equals(grupoOrigem)) {
                return adversarioPotencial;
            }
        }
        return listaAdversarios.get(listaAdversarios.size() - 1); // Fallback
    }

    private List<EstatisticasEquipe> rankTeams(List<Equipe> equipes, List<Grupo> grupos) {
        Map<Long, EstatisticasEquipe> estatisticasMap = new HashMap<>();
        equipes.forEach(equipe -> estatisticasMap.put(equipe.getId(), new EstatisticasEquipe(equipe)));

        List<Jogo> todosOsJogosDosGrupos = jogoRepository.findByGrupoIn(grupos);

        for (Jogo jogo : todosOsJogosDosGrupos) {
            EstatisticasEquipe statsA = estatisticasMap.get(jogo.getEquipeA().getId());
            EstatisticasEquipe statsB = estatisticasMap.get(jogo.getEquipeB().getId());

            Integer placarA = jogo.getPlacarEquipeA();
            Integer placarB = jogo.getPlacarEquipeB();

            if (placarA != null && placarB != null) {
                if (statsA != null) {
                    statsA.golsPro += placarA;
                    statsA.golsContra += placarB;
                    if (jogo.getVencedor() == null) statsA.pontos += 1;
                    else if (jogo.getVencedor().getId().equals(statsA.getEquipe().getId())) statsA.pontos += 3;
                }
                if (statsB != null) {
                    statsB.golsPro += placarB;
                    statsB.golsContra += placarA;
                    if (jogo.getVencedor() == null) statsB.pontos += 1;
                    else if (jogo.getVencedor().getId().equals(statsB.getEquipe().getId())) statsB.pontos += 3;
                }
            }
        }

        return estatisticasMap.values().stream()
                .sorted(Comparator.comparingInt(EstatisticasEquipe::getPontos).reversed()
                        .thenComparingInt(EstatisticasEquipe::getSaldoDeGols).reversed()
                        .thenComparingInt(EstatisticasEquipe::getGolsPro).reversed())
                .collect(Collectors.toList());
    }

    private Etapa definirEtapa(int tamanhoDaChave) {
        if (tamanhoDaChave <= 2) return Etapa.FINAL;
        if (tamanhoDaChave <= 4) return Etapa.SEMIFINAL;
        if (tamanhoDaChave <= 8) return Etapa.QUARTAS_DE_FINAL;
        return Etapa.QUARTAS_DE_FINAL; // Padrão
    }

    private Jogo criarJogo(Equipe equipeA, Equipe equipeB, Etapa etapa) {
        Jogo jogo = new Jogo();
        jogo.setEquipeA(equipeA);
        jogo.setEquipeB(equipeB);
        jogo.setEtapa(etapa);
        jogo.setDataHora(LocalDateTime.now());
        return jogo;
    }

    private void validarFimDaFaseDeGrupos(List<Grupo> grupos) {
        List<Jogo> todosOsJogos = jogoRepository.findByGrupoIn(grupos);
        for (Jogo jogo : todosOsJogos) {
            if (jogo.getPlacarEquipeA() == null || jogo.getPlacarEquipeB() == null) {
                throw new RuntimeException("Não é possível gerar a fase eliminatória. O jogo ID " + jogo.getId() + " ainda não tem um resultado.");
            }
        }
    }

    private static class EstatisticasEquipe {
        Equipe equipe;
        int pontos = 0, golsPro = 0, golsContra = 0;
        EstatisticasEquipe(Equipe equipe) { this.equipe = equipe; }
        int getPontos() { return pontos; }
        int getGolsPro() { return golsPro; }
        int getSaldoDeGols() { return golsPro - golsContra; }
        Equipe getEquipe() { return equipe; }
    }
}