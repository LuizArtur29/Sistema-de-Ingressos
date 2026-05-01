package com.vendaingressos.repository;

import com.vendaingressos.model.TipoIngresso;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoIngressoRepository extends JpaRepository<TipoIngresso, Long> {
    List<TipoIngresso> findBySessaoIdSessao(Long idSessao);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TipoIngresso t WHERE t.idTipoIngresso = :id")
    Optional<TipoIngresso> findByIdForUpdate(@Param("id") Long id);
}
