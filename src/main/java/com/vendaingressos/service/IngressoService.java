package com.vendaingressos.service;

import com.vendaingressos.model.Ingresso;
import com.vendaingressos.repository.EventoRepository;
import com.vendaingressos.repository.IngressoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IngressoService {


    private final IngressoRepository ingressoRepository;
    private final EventoRepository eventoRepository;

    @Autowired
    public IngressoService(IngressoRepository ingressoRepository, EventoRepository eventoRepository) {
        this.ingressoRepository = ingressoRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional
    public Ingresso criarIngressoParaEvento(Ingresso ingresso) {
        return ingressoRepository.save(ingresso);
    }

    @Transactional(readOnly = true)
    public Optional<Ingresso> buscarIngressoPorId(Long id) {
        return ingressoRepository.findById(id);
    }

    /*@Transactional(readOnly = true)
    public boolean temIngressosDisponiveis() {} (Parte do EventoService?) */

    /*@Transactional(readOnly = true)
    public List<Ingresso>listarIngressosPorUsuario() {} (Parte do UsuarioService) */

    @Transactional(readOnly = true)
    public boolean isIngressoValido(Long id) {
        Ingresso ingresso = ingressoRepository.findById(id).orElseThrow(() -> new RuntimeException("Ingresso não encontrado"));
        return ingresso.isIngressoDisponivel();

    }

    @Transactional(readOnly = true)
    public void registrarEntrada(Long id) {
        Ingresso ingresso = ingressoRepository.findById(id).orElseThrow(() -> new RuntimeException("Ingresso não encontrado"));
        ingresso.setIngressoDisponivel(false);

    }

    /*@Transactional(readOnly = true)
    public long contarIngressosVendidos(Long eventoId) {} (Parte de compras)*/


}