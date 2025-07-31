package com.vendaingressos.repository;

import com.vendaingressos.model.Ingresso;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IngressoRepository extends JpaRepository<Ingresso, Long> {
    Long countBySessaoEventoIdSessao(Long sessaoEventoId);
}
