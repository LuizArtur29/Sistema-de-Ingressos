package com.vendaingressos.repository;

import com.vendaingressos.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompraRepository extends JpaRepository<Compra, UUID> {
    List<Compra> findByUsuarioIdUsuario(UUID usuarioId);
}