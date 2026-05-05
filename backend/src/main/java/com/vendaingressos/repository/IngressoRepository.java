package com.vendaingressos.repository;

import com.vendaingressos.model.Ingresso;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngressoRepository extends JpaRepository<Ingresso, Long> {
    Long countBySessaoEventoIdSessao(Long sessaoEventoId);

    List<Ingresso> findByTipoIngressoIdTipoIngresso(Long idTipoIngresso);

    long countBySessaoEventoIdSessaoAndVendidoTrue(Long sessaoEventoId);

    long countBySessaoEventoIdSessaoAndTipoIngressoIdTipoIngressoAndIngressoDisponivelTrueAndVendidoFalse(
            Long sessaoEventoId,
            Long tipoIngressoId
    );

    List<Ingresso> findByCompraIdCompra(Long compraId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Ingresso i WHERE i.idIngresso = :ingressoId")
    Optional<Ingresso> findByIdForUpdate(@Param("ingressoId") Long ingressoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i
            FROM Ingresso i
            WHERE i.sessaoEvento.idSessao = :sessaoEventoId
              AND i.tipoIngresso.idTipoIngresso = :tipoIngressoId
              AND i.ingressoDisponivel = true
              AND i.vendido = false
            ORDER BY i.idIngresso
            """)
    List<Ingresso> findDisponiveisParaVendaComLock(
            @Param("sessaoEventoId") Long sessaoEventoId,
            @Param("tipoIngressoId") Long tipoIngressoId,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i
            FROM Ingresso i
            WHERE i.sessaoEvento.idSessao = :sessaoEventoId
              AND i.tipoIngresso.idTipoIngresso = :tipoIngressoId
              AND i.ingressoDisponivel = true
              AND i.vendido = false
              AND i.idIngresso <> :excluirId
            ORDER BY i.idIngresso
            """)
    List<Ingresso> findDisponiveisParaVendaComLockExcluindo(
            @Param("sessaoEventoId") Long sessaoEventoId,
            @Param("tipoIngressoId") Long tipoIngressoId,
            @Param("excluirId") Long excluirId,
            Pageable pageable
    );
}
