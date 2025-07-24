package com.vendaingressos.repository;

import com.vendaingressos.model.Ingresso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IngressoRepository extends JpaRepository<Ingresso, UUID> {
}
