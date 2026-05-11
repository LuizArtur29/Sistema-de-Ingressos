package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    @Autowired
    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Transactional
    public Evento salvarEvento(Evento evento) {
        // Validação da data: garantir que dataFim não seja antes de dataInicio
        if (evento.getDataFim().isBefore(evento.getDataInicio())) {
            throw new BadRequestException("A data de fim do evento não pode ser anterior à data de início.");
        }
        return eventoRepository.save(evento);
    }

    @Transactional(readOnly = true)
    public List<Evento> buscarTodosEventos() {
        return eventoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Evento> buscarEventoPorId(Long id) {
        return eventoRepository.findById(id);
    }

    @Transactional
    public void deletarEvento(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evento não encontrado com ID: " + id);
        }
        eventoRepository.deleteById(id);
    }

    @Transactional
    public Evento atualizarEvento(Long id, Evento eventoAtualizado) {
        return eventoRepository.findById(id).map(evento -> {
            evento.setNome(eventoAtualizado.getNome());
            evento.setDescricao(eventoAtualizado.getDescricao());
            evento.setDataInicio(eventoAtualizado.getDataInicio()); // Atualizado
            evento.setDataFim(eventoAtualizado.getDataFim()); // Atualizado
            evento.setLocal(eventoAtualizado.getLocal());
            evento.setCapacidadeTotal(eventoAtualizado.getCapacidadeTotal());
            evento.setStatus(eventoAtualizado.getStatus());
            // Não há mais listaIngressos diretamente no Evento
            return eventoRepository.save(evento);
        }).orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID: " + id));
    }
}