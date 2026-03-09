package com.vendaingressos.service;

import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Administrador;
import com.vendaingressos.repository.AdministradorRepository; // Necessário criar a interface JpaRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Administrador cadastrar(Administrador admin) {
        admin.setSenha(passwordEncoder.encode(admin.getSenha()));
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