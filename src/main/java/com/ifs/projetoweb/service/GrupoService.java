package com.ifs.projetoweb.service;

import com.ifs.projetoweb.entity.*;
import com.ifs.projetoweb.repository.CompeticaoRepository;
import com.ifs.projetoweb.repository.EquipeRepository;
import com.ifs.projetoweb.repository.GrupoRepository;
import com.ifs.projetoweb.repository.JogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GrupoService {

    @Autowired
    private CompeticaoRepository competicaoRepository;
    @Autowired
    private EquipeRepository equipeRepository;
    @Autowired
    private GrupoRepository grupoRepository;
    @Autowired
    private JogoRepository jogoRepository;

    @Transactional
    public List<Grupo> gerarGrupos(Long competicaoId) {
        System.out.println("--- INICIANDO GERAÇÃO DE GRUPOS ---");

        Competicao competicao = competicaoRepository.findById(competicaoId)
                .orElseThrow(() -> new RuntimeException("Competição não encontrada!"));
        System.out.println("Competição encontrada: " + competicao.getId());

        List<Equipe> equipes = equipeRepository.findByCompeticao(competicao);
        System.out.println("Total de equipes encontradas: " + equipes.size());
        if (equipes.size() < 3) {
            throw new RuntimeException("São necessárias no mínimo 3 equipes para gerar os grupos.");
        }

        Collections.shuffle(equipes);
        System.out.println("Equipes embaralhadas.");

        List<List<Equipe>> listaDeGruposDeEquipes = new ArrayList<>();
        int tamanhoBaseDoGrupo = 3;
        for (int i = 0; i < equipes.size(); i += tamanhoBaseDoGrupo) {
            int fim = Math.min(i + tamanhoBaseDoGrupo, equipes.size());
            listaDeGruposDeEquipes.add(new ArrayList<>(equipes.subList(i, fim)));
        }

        if (listaDeGruposDeEquipes.size() > 1) {
            List<Equipe> ultimoGrupo = listaDeGruposDeEquipes.get(listaDeGruposDeEquipes.size() - 1);
            if (ultimoGrupo.size() < tamanhoBaseDoGrupo) {
                List<Equipe> penultimoGrupo = listaDeGruposDeEquipes.get(listaDeGruposDeEquipes.size() - 2);
                penultimoGrupo.addAll(ultimoGrupo);
                listaDeGruposDeEquipes.remove(ultimoGrupo);
            }
        }
        System.out.println("Equipes divididas em " + listaDeGruposDeEquipes.size() + " grupos.");

        List<Grupo> gruposSalvos = new ArrayList<>();
        int contadorGrupo = 1;
        for (List<Equipe> grupoDeEquipes : listaDeGruposDeEquipes) {
            System.out.println("Processando Grupo " + contadorGrupo + " com " + grupoDeEquipes.size() + " equipes.");
            Grupo novoGrupo = new Grupo();
            novoGrupo.setNome("Grupo " + contadorGrupo);
            novoGrupo.setCompeticao(competicao);
            novoGrupo.setEquipes(grupoDeEquipes);
            Grupo grupoSalvo = grupoRepository.save(novoGrupo); // Salva e pega a referência atualizada

            gerarJogosDoGrupo(grupoSalvo); // Passa a referência que acabou de ser salva

            gruposSalvos.add(grupoSalvo);
            contadorGrupo++;
        }
        System.out.println("--- FIM DA GERAÇÃO DE GRUPOS ---");
        return gruposSalvos;
    }

    private void gerarJogosDoGrupo(Grupo grupo) {
        List<Equipe> equipesDoGrupo = grupo.getEquipes();
        System.out.println(">> Entrando em gerarJogosDoGrupo para o " + grupo.getNome() + ". Número de equipes no grupo: " + equipesDoGrupo.size());

        if (equipesDoGrupo.size() < 2) {
            System.out.println(">> Pulei a criação de jogos, menos de 2 equipes.");
            return;
        }

        int jogosCriados = 0;
        for (int i = 0; i < equipesDoGrupo.size(); i++) {
            for (int j = i + 1; j < equipesDoGrupo.size(); j++) {
                Jogo novoJogo = new Jogo();
                novoJogo.setEquipeA(equipesDoGrupo.get(i));
                novoJogo.setEquipeB(equipesDoGrupo.get(j));
                novoJogo.setGrupo(grupo);
                novoJogo.setEtapa(Etapa.FASE_DE_GRUPOS);
                novoJogo.setDataHora(LocalDateTime.now());

                jogoRepository.save(novoJogo);
                jogosCriados++;
            }
        }
        System.out.println(">> Criei " + jogosCriados + " jogos para o " + grupo.getNome());
    }

    @Transactional(readOnly = true) // readOnly = true indica que este método só lê dados
    public List<Grupo> getGruposByCompeticao(Long competicaoId) {
        Competicao competicao = competicaoRepository.findById(competicaoId)
                .orElseThrow(() -> new RuntimeException("Competição de diagnóstico não encontrada!"));

        // Usando o método que força o carregamento das equipes para garantir
        List<Grupo> grupos = grupoRepository.findByCompeticaoWithEquipes(competicao);
        System.out.println("DIAGNÓSTICO: Encontrados " + grupos.size() + " grupos para a competição ID " + competicaoId);
        return grupos;
    }
}
