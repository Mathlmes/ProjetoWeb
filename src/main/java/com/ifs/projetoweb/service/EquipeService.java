package com.ifs.projetoweb.service;


import com.ifs.projetoweb.dto.EquipeCreateDTO;
import com.ifs.projetoweb.entity.Competicao;
import com.ifs.projetoweb.entity.Curso;
import com.ifs.projetoweb.entity.Equipe;
import com.ifs.projetoweb.entity.Usuario;
import com.ifs.projetoweb.repository.CompeticaoRepository;
import com.ifs.projetoweb.repository.CursoRepository;
import com.ifs.projetoweb.repository.EquipeRepository;
import com.ifs.projetoweb.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private CompeticaoRepository competicaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;



    @Transactional // garante que ou toda a operaçao funciona, ou nada é salvo no banco.
    public Equipe create(EquipeCreateDTO dto) {

        //orElseThrow() para parar a execução se um ID for inválido.
        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado!"));

        Competicao competicao = competicaoRepository.findById(dto.getCompeticaoId())
                .orElseThrow(() -> new RuntimeException("Competição não encontrada!"));

        Usuario tecnico = usuarioRepository.findById(dto.getTecnicoId())
                .orElseThrow(() -> new RuntimeException("Usuário (técnico) não encontrado!"));

        List<Usuario> atletas = usuarioRepository.findAllById(dto.getAtletaIds());
        // valida se todos os atletas da lista de ids foram encontrados
        if (atletas.size() != dto.getAtletaIds().size()) {
            throw new RuntimeException("Um ou mais atletas da lista não foram encontrados!");
        }
        int minAtletas = competicao.getEsporte().getMinAtletas();
        int maxAtletas = competicao.getEsporte().getMaxAtletas();

        if(atletas.size() < minAtletas || atletas.size() > maxAtletas) {
            throw new RuntimeException("O número de atletas (" + atletas.size() + ") não está de acordo com as regras do esporte (Mín: " + minAtletas + ", Máx: " + maxAtletas + ").");
        }
        Optional<Equipe> equipeExistente = equipeRepository.findByCursoAndCompeticao(curso, competicao);
        if (equipeExistente.isPresent()) {
            throw new RuntimeException("Já existe uma equipe deste curso para esta competição.");
        }

        Equipe novaEquipe = new Equipe();
        novaEquipe.setNome(dto.getNome());
        novaEquipe.setCurso(curso);
        novaEquipe.setTecnico(tecnico);
        novaEquipe.setAtletas(atletas);
        // para conectar a equipe a competição
        novaEquipe.setCompeticao(competicao);

        return equipeRepository.save(novaEquipe);
        }


    }
