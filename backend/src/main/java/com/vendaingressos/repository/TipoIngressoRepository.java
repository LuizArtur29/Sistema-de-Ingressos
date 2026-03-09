package com.vendaingressos.repository;

import com.vendaingressos.model.TipoIngresso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoIngressoRepository extends JpaRepository<TipoIngresso, Long> {
    List<TipoIngresso> findBySessaoIdSessao(Long idSessao);
}
