package com.vendaingressos.service;

import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.TipoIngresso;
import com.vendaingressos.repository.TipoIngressoRepository; // Necessário criar a interface JpaRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TipoIngressoService {

    @Autowired
    private TipoIngressoRepository tipoIngressoRepository;

    @Transactional
    public TipoIngresso salvar(TipoIngresso tipo) {
        return tipoIngressoRepository.save(tipo);
    }

    @Transactional(readOnly = true)
    public List<TipoIngresso> listarPorSessao(Long sessaoId) {
        return tipoIngressoRepository.findBySessaoIdSessao(sessaoId);
    }
}