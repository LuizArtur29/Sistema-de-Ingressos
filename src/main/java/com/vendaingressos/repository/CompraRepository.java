package com.vendaingressos.repository;

import com.vendaingressos.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByUsuarioIdUsuario(Long usuarioId);
    // Alterado para buscar por ID da SessaoEvento, não mais por Evento
    List<Compra> findByIngressoSessaoEventoIdSessao(Long sessaoEventoId);

    @Query("SELECT SUM(c.quantidadeIngressos) FROM Compra c WHERE c.ingresso.sessaoEvento.idSessao = :sessaoEventoId")
    Long sumQuantidadeIngressosByIngressoSessaoEventoIdSessao(@Param("sessaoEventoId") Long sessaoEventoId);
}