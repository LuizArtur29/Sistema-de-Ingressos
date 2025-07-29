package com.vendaingressos.repository;

import com.vendaingressos.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByUsuarioIdUsuario(Long usuarioId);
    // Alterado para buscar por ID da SessaoEvento, não mais por Evento
    List<Compra> findByIngressoSessaoEventoIdSessao(Long sessaoEventoId);

}