package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.Transferencia;
import com.vendaingressos.repository.TransferenciaRepository; // Necessário criar a interface JpaRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferenciaService {

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @Autowired
    private IngressoService ingressoService;

    @Transactional
    public Transferencia realizarTransferencia(Transferencia transferencia) {
        Ingresso ingresso = transferencia.getIngressoTransferido();

        // Verifica se o ingresso é válido para transferência
        if (!ingressoService.isIngressoValido(ingresso.getIdIngresso())) {
            throw new BadRequestException("Ingresso inválido ou já utilizado.");
        }

        // Lógica adicional: O ingresso deve pertencer ao vendedor antes da troca
        return transferenciaRepository.save(transferencia);
    }
}