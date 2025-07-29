package com.vendaingressos.repository;

import com.vendaingressos.model.SessaoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessaoEventoRepository extends JpaRepository<SessaoEvento, Long> {
    // Você pode adicionar métodos de consulta personalizados aqui, se necessário.
    List<SessaoEvento> findByEventoPaiId(Long eventoPaiId);
}