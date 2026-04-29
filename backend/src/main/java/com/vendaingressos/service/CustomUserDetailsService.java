package com.vendaingressos.service;

import com.vendaingressos.model.Administrador;
import com.vendaingressos.model.Usuario;
import com.vendaingressos.model.enums.Role;
import com.vendaingressos.repository.AdministradorRepository;
import com.vendaingressos.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository, AdministradorRepository administradorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario != null) {
            String role = mapRole(usuario.getRole(), Role.USUARIO);
            return new User(usuario.getEmail(), usuario.getSenha(), List.of(new SimpleGrantedAuthority(role))
            );
        }

        Administrador admin = administradorRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            String role = mapRole(admin.getRole(), Role.ADMINISTRADOR);
            return new User(admin.getEmail(), admin.getSenha(), List.of(new SimpleGrantedAuthority(role))
            );
        }

        throw new UsernameNotFoundException("Usuário não encontrado com e-mail: " + email);
    }

    private String mapRole(Role role, Role fallback) {
        Role resolved = role == null ? fallback : role;
        if (resolved == Role.ADMINISTRADOR) {
            return "ROLE_ADMIN";
        }
        return "ROLE_USER";
    }
}