package com.vendaingressos.repository;

import com.vendaingressos.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {
<<<<<<< Updated upstream
    List<Compra> findByUsuarioIdUsuario(UUID usuarioId);
=======
    List<Compra> findByUsuarioIdUsuario(Long usuarioId);
>>>>>>> Stashed changes
}