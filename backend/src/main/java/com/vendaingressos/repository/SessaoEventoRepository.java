package com.vendaingressos.repository;

import com.vendaingressos.model.SessaoEvento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessaoEventoRepository extends JpaRepository<SessaoEvento, Long> {
    // Você pode adicionar métodos de consulta personalizados aqui, se necessário.
    List<SessaoEvento> findByEventoPaiId(Long eventoPaiId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SessaoEvento s WHERE s.idSessao = :sessaoId")
    Optional<SessaoEvento> findByIdForUpdate(@Param("sessaoId") Long sessaoId);
}