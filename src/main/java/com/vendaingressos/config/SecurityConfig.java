package com.vendaingressos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita proteção CSRF (comum em APIs REST)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // REGRA DE OURO: Permite acesso a TUDO para TODOS
                );
        return http.build();
    }

    // Mantemos este Bean para que o UsuarioService continue a conseguir
    // criptografar as senhas ao salvar no banco de dados.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}