package com.ifs.projetoweb.service;


import com.ifs.projetoweb.dto.UsuarioDTO;
import com.ifs.projetoweb.entity.TipoUsuario;
import com.ifs.projetoweb.entity.Usuario;
import com.ifs.projetoweb.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

@Autowired
    private UsuarioRepository usuarioRepository;

@Transactional
    public Usuario registrarAtleta(UsuarioDTO dto) {

    if(usuarioRepository.findByMatricula(dto.getMatricula()).isPresent()) {
        throw new RuntimeException("Matricula já cadastrada");
    }

    Usuario novoUsuario = new Usuario();

    //Mapeamento dos dados do DTO para a Entidade
    novoUsuario.setNomeCompleto(dto.getNomeCompleto());
    novoUsuario.setEmail(dto.getEmail());
    novoUsuario.setSenha(dto.getSenha());
    novoUsuario.setMatricula(dto.getMatricula());
    novoUsuario.setTelefone(dto.getTelefone());
    novoUsuario.setApelido(dto.getApelido());

    novoUsuario.setTipoUsuario(TipoUsuario.ATLETA);
    return usuarioRepository.save(novoUsuario);

}

@Transactional
    public Usuario criarCoordenador(UsuarioDTO dto) {
    if(usuarioRepository.findByMatricula(dto.getMatricula()).isPresent()) {
        throw new RuntimeException("Matricula já cadastrada");
    }
    Usuario novoUsuario = new Usuario();
    String senhaGerada = gerarSenhaAleatoria();
    System.out.println("SIMULANDO ENVIO DE E-MAIL: Senha para " + dto.getEmail() + " é: " + senhaGerada);
    novoUsuario.setNomeCompleto(dto.getNomeCompleto());
    novoUsuario.setEmail(dto.getEmail());
    novoUsuario.setSenha(senhaGerada); // No requisito, a senha seria auto-gerada
    novoUsuario.setMatricula(dto.getMatricula());
    novoUsuario.setTelefone(dto.getTelefone());
    novoUsuario.setApelido(dto.getApelido());
    novoUsuario.setTipoUsuario(TipoUsuario.COORDENADOR);

    return usuarioRepository.save(novoUsuario);
}

public String gerarSenhaAleatoria() {
    return "ifs" + (int)(Math.random() * 1000);
}

public Usuario criarArbitro(UsuarioDTO dto) {
    if (usuarioRepository.findByMatricula(dto.getMatricula()).isPresent()) {
        throw new RuntimeException("Erro: Matrícula já cadastrada.");
    }

    Usuario novoUsuario = new Usuario();
    novoUsuario.setNomeCompleto(dto.getNomeCompleto());
    novoUsuario.setEmail(dto.getEmail());
    novoUsuario.setSenha(dto.getSenha());
    novoUsuario.setMatricula(dto.getMatricula());
    novoUsuario.setTelefone(dto.getTelefone());
    novoUsuario.setApelido(dto.getApelido());
    novoUsuario.setTipoUsuario(TipoUsuario.ARBITRO);

    return usuarioRepository.save(novoUsuario);
}
public Usuario findByMatricula(String matricula) {
    return usuarioRepository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com a matrícula: " + matricula));
}

}
