package com.vendaingressos.config;

import com.vendaingressos.model.Administrador;
import com.vendaingressos.model.enums.Role;
import com.vendaingressos.repository.AdministradorRepository;
import com.vendaingressos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AdminBootstrapRunner implements CommandLineRunner {

    private final AdministradorRepository administradorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    @Value("${security.bootstrap-admin.enabled:false}")
    private boolean enabled;

    @Value("${security.bootstrap-admin.nome:}")
    private String nome;

    @Value("${security.bootstrap-admin.email:}")
    private String email;

    @Value("${security.bootstrap-admin.senha:}")
    private String senha;

    @Value("${security.bootstrap-admin.telefone:}")
    private String telefone;

    public AdminBootstrapRunner(
            AdministradorRepository administradorRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.administradorRepository = administradorRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!enabled) {
            return;
        }
        if (administradorRepository.count() > 0) {
            return;
        }
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            return;
        }
        if (usuarioRepository.findByEmail(email).isPresent()) {
            log.warn("Bootstrap admin ignorado: email já existe em Usuario: {}", email);
            return;
        }

        Administrador admin = new Administrador();
        admin.setNome((nome == null || nome.isBlank()) ? "Admin Bootstrap" : nome);
        admin.setEmail(email);
        admin.setSenha(passwordEncoder.encode(senha));
        admin.setTelefone((telefone == null) ? "" : telefone);
        admin.setRole(Role.ADMINISTRADOR);

        administradorRepository.save(admin);
    }
}
