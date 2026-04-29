package com.vendaingressos.service;

import com.vendaingressos.dto.UsuarioCreateRequest;
import com.vendaingressos.dto.UsuarioUpdateRequest;
import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Compra;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.model.enums.Role;
import com.vendaingressos.repository.AdministradorRepository;
import com.vendaingressos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {


    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompraService compraService;
    private final AdministradorRepository administradorRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, CompraService compraService, AdministradorRepository administradorRepository){
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.compraService = compraService;
        this.administradorRepository = administradorRepository;
    }

    @Transactional
    public Usuario cadastrarUsuario(UsuarioCreateRequest dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent() || administradorRepository.findByEmail(dto.email()).isPresent()) {
            throw new BadRequestException("Já existe um usuário ou administrador cadastrado com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setCpf(dto.cpf());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setEndereco(dto.endereco());
        usuario.setTelefone(dto.telefone());
        usuario.setRole(Role.USUARIO);

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodosUsuarios(){
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorId(Long id){
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public Usuario atualizarUsuario(Long id, UsuarioUpdateRequest usuarioAtualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            if (usuarioAtualizado.nome() != null && !usuarioAtualizado.nome().isBlank()) {
                usuario.setNome(usuarioAtualizado.nome());
            }

            if (usuarioAtualizado.dataNascimento() != null) {
                usuario.setDataNascimento(usuarioAtualizado.dataNascimento());
            }

            if (usuarioAtualizado.email() != null && !usuarioAtualizado.email().isBlank()
                    && !usuarioAtualizado.email().equalsIgnoreCase(usuario.getEmail())) {
                if (usuarioRepository.findByEmail(usuarioAtualizado.email()).isPresent()
                        || administradorRepository.findByEmail(usuarioAtualizado.email()).isPresent()) {
                    throw new BadRequestException("Já existe um usuário ou administrador cadastrado com este e-mail: " + usuarioAtualizado.email());
                }
                usuario.setEmail(usuarioAtualizado.email());
            }

            if (usuarioAtualizado.endereco() != null && !usuarioAtualizado.endereco().isBlank()) {
                usuario.setEndereco(usuarioAtualizado.endereco());
            }

            if (usuarioAtualizado.telefone() != null && !usuarioAtualizado.telefone().isBlank()) {
                usuario.setTelefone(usuarioAtualizado.telefone());
            }

            if (usuarioAtualizado.senha() != null && !usuarioAtualizado.senha().isBlank()) {
                usuario.setSenha(passwordEncoder.encode(usuarioAtualizado.senha()));
            }

            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
    }

    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Ingresso> listarIngressosPorUsuario(Long usuarioId) {
        // Primeiro, verifica se o usuário existe
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId);
        }
        // Busca todas as compras do usuário
        List<Compra> comprasDoUsuario = compraService.buscarComprasPorUsuario(usuarioId);

        // Extrai os ingressos de cada compra
        return comprasDoUsuario.stream()
                .map(Compra::getIngresso)
                .collect(Collectors.toList());
    }
}
