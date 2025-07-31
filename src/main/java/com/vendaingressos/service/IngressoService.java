package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.SessaoEvento; // Importar a nova entidade
import com.vendaingressos.repository.EventoRepository;
import com.vendaingressos.repository.IngressoRepository;
import com.vendaingressos.repository.SessaoEventoRepository; // Importar o novo repositório
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Adicionar para usar .stream().collect()

@Service
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final SessaoEventoRepository sessaoEventoRepository; // Novo repositório injetado

    @Autowired
    public IngressoService(IngressoRepository ingressoRepository, SessaoEventoRepository sessaoEventoRepository) {
        this.ingressoRepository = ingressoRepository;
        this.sessaoEventoRepository = sessaoEventoRepository;
    }

    @Transactional
    public Ingresso criarIngressoParaSessaoEvento(Long sessaoEventoId, Ingresso ingresso) {
        SessaoEvento sessaoEvento = sessaoEventoRepository.findById(sessaoEventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de Evento não encontrada com ID: " + sessaoEventoId));

        // Obter a capacidade total do evento pai (capacidade por dia)
        Integer capacidadeTotalEvento = sessaoEvento.getEventoPai().getCapacidadeTotal();

        // Contar quantos ingressos já foram criados para esta sessão específica
        long ingressosExistentesNestaSessao = ingressoRepository.countBySessaoEventoIdSessao(sessaoEventoId);

        if (ingressosExistentesNestaSessao >= capacidadeTotalEvento) {
            throw new BadRequestException("A sessão do evento atingiu sua capacidade máxima de ingressos.");
        }

        ingresso.setSessaoEvento(sessaoEvento);
        return ingressoRepository.save(ingresso);
    }

    @Transactional(readOnly = true)
    public List<Ingresso> listarIngressosPorSessaoEvento(Long sessaoEventoId) {
        SessaoEvento sessaoEvento = sessaoEventoRepository.findById(sessaoEventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de Evento não encontrada"));

        // Filtra os ingressos associados a esta sessão específica
        return ingressoRepository.findAll().stream()
                .filter(ingresso -> ingresso.getSessaoEvento().getIdSessao().equals(sessaoEventoId))
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Optional<Ingresso> buscarIngressoPorId(Long ingressoId) {
        return ingressoRepository.findById(ingressoId);
    }

    @Transactional
    public boolean isIngressoValido(Long ingressoId) {
        Ingresso ingresso = ingressoRepository.findById(ingressoId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));
        return ingresso.isIngressoDisponivel();
    }

    @Transactional
    public void registrarEntrada(Long ingressoId) {
        Ingresso ingresso = ingressoRepository.findById(ingressoId)
                .orElseThrow(() -> new ResourceNotFoundException("Ingresso não encontrado"));

        if (!ingresso.isIngressoDisponivel()) {
            throw new BadRequestException("Ingresso já utilizado ou não disponível.");
        }
        ingresso.setIngressoDisponivel(false);
        ingressoRepository.save(ingresso);
    }
}