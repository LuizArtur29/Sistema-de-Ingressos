package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
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
    public Ingresso criarIngressoParaEvento(Long eventoId, Ingresso ingresso) {
        Evento evento = eventoRepository.findById(eventoId).orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID:" + eventoId));

        if (evento.getCapacidadeTotal() <= evento.getListaIngressos().size()) {
            throw new BadRequestException("O evento atingiu sua capacidade máxima de ingressos.");

        }

        ingresso.setEvento(evento);

        evento.getListaIngressos().add(ingresso);

        return ingressoRepository.save(ingresso);
    }

    @Transactional(readOnly = true)
    public List<Ingresso> listarIngressosPorEvento(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId).orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));

        return evento.getListaIngressos();
    }

    @Transactional(readOnly = true)
    public Optional<Ingresso> buscarIngressoPorId(Long ingressoId) {
        return ingressoRepository.findById(ingressoId);
    }

    /*@Transactional(readOnly = true)
    public boolean temIngressosDisponiveis() {} (Parte do EventoService?) */

    /*@Transactional(readOnly = true)
    public List<Ingresso>listarIngressosPorUsuario() {} (Parte do UsuarioService) */

    @Transactional
    public boolean isIngressoValido(Long ingressoId) {
        Ingresso ingresso = ingressoRepository.findById(ingressoId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));
        return ingresso.isIngressoDisponivel();
    }

    @Transactional(readOnly = true)
    public void registrarEntrada(Long ingressoId) {
        Ingresso ingresso = ingressoRepository.findById(ingressoId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));

        if (!ingresso.isIngressoDisponivel()) {
            throw new BadRequestException("Ingresso já utilizado ou não disponível.");
        }
        ingresso.setIngressoDisponivel(false);
        ingressoRepository.save(ingresso);
    }

    /*@Transactional(readOnly = true)
    public long contarIngressosVendidos(Long eventoId) {} (Parte de compras)*/


}