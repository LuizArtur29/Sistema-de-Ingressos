package com.vendaingressos.repository;

import com.vendaingressos.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    @Query("SELECT DISTINCT c FROM Compra c LEFT JOIN FETCH c.ingressos WHERE c.usuario.idUsuario = :usuarioId")
    List<Compra> findByUsuarioIdUsuarioFetchIngressos(@Param("usuarioId") Long usuarioId);

    @Query("SELECT DISTINCT c FROM Compra c LEFT JOIN FETCH c.ingressos")
    List<Compra> findAllFetchIngressos();

    @Query("SELECT c FROM Compra c LEFT JOIN FETCH c.ingressos WHERE c.idCompra = :id")
    Optional<Compra> findByIdFetchIngressos(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Compra c JOIN c.ingressos i WHERE i.sessaoEvento.idSessao = :sessaoEventoId")
    List<Compra> findDistinctByIngressosSessaoId(@Param("sessaoEventoId") Long sessaoEventoId);

    @Query("SELECT COALESCE(SUM(c.quantidadeIngressos), 0) FROM Compra c WHERE c.idCompra IN "
            + "(SELECT DISTINCT i.compra.idCompra FROM Ingresso i WHERE i.sessaoEvento.idSessao = :sessaoEventoId AND i.compra IS NOT NULL)")
    Long sumQuantidadeIngressosByIngressosSessao(@Param("sessaoEventoId") Long sessaoEventoId);
}
