package com.vendaingressos.repository;

import com.vendaingressos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByEmail(String email);
}
