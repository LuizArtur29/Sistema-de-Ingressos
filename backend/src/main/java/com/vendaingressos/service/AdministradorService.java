package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Administrador;
import com.vendaingressos.model.enums.Role;
import com.vendaingressos.repository.AdministradorRepository;
import com.vendaingressos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorService(AdministradorRepository administradorRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Administrador cadastrar(Administrador admin) {
        if (administradorRepository.findByEmail(admin.getEmail()).isPresent() || usuarioRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new BadRequestException("Já existe um cadastro com este e-mail.");
        }
        admin.setSenha(passwordEncoder.encode(admin.getSenha()));
        admin.setRole(Role.ADMINISTRADOR);
        return administradorRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public List<Administrador> listarTodos() {
        return administradorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Administrador buscarPorId(Long id) {
        return administradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado"));
    }
}