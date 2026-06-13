package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.repository.AdministradorRepository;
import com.vendaingressos.repository.EventoRepository;
import com.vendaingressos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;

    @Autowired
    public EventoService(EventoRepository eventoRepository, UsuarioRepository usuarioRepository, AdministradorRepository administradorRepository) {
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
    }

    @Transactional
    public Evento salvarEvento(Evento evento, String email) {
        // Validação da data: garantir que dataFim não seja antes de dataInicio
        if (evento.getDataFim().isBefore(evento.getDataInicio())) {
            throw new BadRequestException("A data de fim do evento não pode ser anterior à data de início.");
        }
        administradorRepository.findByEmail(email).ifPresent(evento::setAdministrador);
        return eventoRepository.save(evento);
    }

    @Transactional(readOnly = true)
    public List<Evento> buscarTodosEventos() {
        return eventoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Evento> buscarMeusEventos(String email) {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            return eventoRepository.findEventsPurchasedByUserEmail(email);
        } else if (administradorRepository.findByEmail(email).isPresent()) {
            return eventoRepository.findByAdministradorEmail(email);
        }
        return List.of();
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