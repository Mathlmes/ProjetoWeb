package com.ifs.projetoweb.service;

import com.ifs.projetoweb.dto.CursoDTO;
import com.ifs.projetoweb.entity.Campus;
import com.ifs.projetoweb.entity.Curso;
import com.ifs.projetoweb.entity.Usuario;
import com.ifs.projetoweb.repository.CampusRepository;
import com.ifs.projetoweb.repository.CursoRepository;
import com.ifs.projetoweb.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private CampusRepository campusRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public Curso create(CursoDTO dto) {
        // Busca as entidades relacionadas pelos IDs
        Campus campus = campusRepository.findById(dto.getCampusId())
                .orElseThrow(() -> new RuntimeException("Campus não encontrado!"));
        Usuario coordenador = usuarioRepository.findById(dto.getCoordenadorId())
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado!"));

        // Cria e salva a nova entidade Curso
        Curso curso = new Curso();
        curso.setNome(dto.getNome());
        curso.setNivel(dto.getTipoNivel());
        curso.setCampus(campus);
        curso.setCoordenador(coordenador);

        return cursoRepository.save(curso);
    }

    public void delete(Long id) {
        if (!cursoRepository.existsById(id)) {
            throw new RuntimeException("Curso não encontrado para deletar!");
        }
        cursoRepository.deleteById(id);
    }
}