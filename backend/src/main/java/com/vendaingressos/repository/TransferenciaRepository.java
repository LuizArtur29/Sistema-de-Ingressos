package com.vendaingressos.repository;

import com.vendaingressos.model.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {
    // Lista todas as transferências realizadas por um vendedor específico
    List<Transferencia> findByVendedorIdUsuario(Long vendedorId);

    // Lista todas as transferências recebidas por um comprador específico
    List<Transferencia> findByCompradorIdUsuario(Long compradorId);

    // Procura o histórico de transferência de um ingresso específico
    List<Transferencia> findByIngressoTransferidoIdIngresso(Long ingressoId);
}
