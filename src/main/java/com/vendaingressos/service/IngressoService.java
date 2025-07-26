package com.vendaingressos.service;

import com.vendaingressos.model.Ingresso;
import com.vendaingressos.repository.EventoRepository;
import com.vendaingressos.repository.IngressoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngressoService {


    private final IngressoRepository ingressoRepository;
    private final EventoRepository eventoRepository;

    @Autowired
    public IngressoService(IngressoRepository ingressoRepository, EventoRepository eventoRepository) {
        this.ingressoRepository = ingressoRepository;
        this.eventoRepository = eventoRepository;
    }

    public Ingresso criarIngressoParaEvento() {}

    public Ingresso buscarIngressoPorId() {}

    public boolean temIngressosDisponiveis() {}

    public List<Ingresso>listarIngressosPorUsuario() {}

    public boolean isIngressoValido(String codigo) {}

    public void registrarEntrada(String codigoIngresso{}

    public long contarIngressosVendidos(Long eventoId) {}



}