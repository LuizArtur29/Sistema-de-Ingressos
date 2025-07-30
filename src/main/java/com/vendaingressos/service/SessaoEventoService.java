package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException; // Se precisar de validações
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.model.SessaoEvento;
import com.vendaingressos.repository.EventoRepository;
import com.vendaingressos.repository.SessaoEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SessaoEventoService {

    private final SessaoEventoRepository sessaoEventoRepository;
    private final EventoRepository eventoRepository; // Para buscar o evento pai

    @Autowired
    public SessaoEventoService(SessaoEventoRepository sessaoEventoRepository, EventoRepository eventoRepository) {
        this.sessaoEventoRepository = sessaoEventoRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional
    public SessaoEvento salvarSessaoEvento(SessaoEvento sessaoEvento) {
        // Validação: garantir que o evento pai exista
        Evento eventoPai = eventoRepository.findById(sessaoEvento.getEventoPai().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento pai não encontrado com ID: " + sessaoEvento.getEventoPai().getId()));

        sessaoEvento.setEventoPai(eventoPai);
        return sessaoEventoRepository.save(sessaoEvento);
    }

    @Transactional(readOnly = true)
    public List<SessaoEvento> buscarTodasSessoesEventos() {
        return sessaoEventoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<SessaoEvento> buscarSessaoEventoPorId(Long id) {
        return sessaoEventoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<SessaoEvento> buscarSessoesPorEventoPai(Long eventoPaiId) {
        // Opcional: verificar se o EventoPai existe antes de buscar as sessões
        if (!eventoRepository.existsById(eventoPaiId)) {
            throw new ResourceNotFoundException("Evento pai não encontrado com ID: " + eventoPaiId);
        }
        return sessaoEventoRepository.findByEventoPaiId(eventoPaiId);
    }

    @Transactional
    public SessaoEvento atualizarSessaoEvento(Long id, SessaoEvento sessaoEventoAtualizada) {
        return sessaoEventoRepository.findById(id).map(sessao -> {
            sessao.setNomeSessao(sessaoEventoAtualizada.getNomeSessao());
            sessao.setDataHoraSessao(sessaoEventoAtualizada.getDataHoraSessao());
            sessao.setStatusSessao(sessaoEventoAtualizada.getStatusSessao());
            // Se o eventoPai puder ser atualizado, adicione lógica para buscar e setar o novo eventoPai
            // Por simplicidade, assumimos que o eventoPai não muda após a criação.

            return sessaoEventoRepository.save(sessao);
        }).orElseThrow(() -> new ResourceNotFoundException("Sessão de Evento não encontrada com ID: " + id));
    }

    @Transactional
    public void deletarSessaoEvento(Long id) {
        if (!sessaoEventoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sessão de Evento não encontrada com ID: " + id);
        }
        sessaoEventoRepository.deleteById(id);
    }
}