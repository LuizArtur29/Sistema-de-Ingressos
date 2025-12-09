package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.redis.EventoRedisCache;
import com.vendaingressos.repository.jdbc.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final EventoRedisCache eventoRedisCache;

    @Autowired
    public EventoService(EventoRepository eventoRepository, EventoRedisCache eventoRedisCache) {
        this.eventoRepository = eventoRepository;
        this.eventoRedisCache = eventoRedisCache;
    }

    @Transactional
    public Evento salvarEvento(Evento evento) {
        if (evento.getDataFim() != null && evento.getDataInicio() != null &&
                evento.getDataFim().isBefore(evento.getDataInicio())) {
            throw new BadRequestException("A data de fim do evento não pode ser anterior à data de início.");
        }

        eventoRepository.salvar(evento);

        // Cache
        eventoRedisCache.cacheEvento(evento);

        return evento;
    }

    @Transactional(readOnly = true)
    public List<Evento> buscarTodosEventos() {
        return eventoRepository.listarTodos();
    }

    @Transactional(readOnly = true)
    public Optional<Evento> buscarEventoPorId(Long id) {
        Optional<Evento> cachedEvento = eventoRedisCache.getCachedEvento(id);
        if (cachedEvento.isPresent()) {
            return cachedEvento;
        }

        Evento evento = eventoRepository.buscarPorId(id);

        if (evento != null) {
            eventoRedisCache.cacheEvento(evento);
            return Optional.of(evento);
        }

        return Optional.empty();
    }

    @Transactional
    public void deletarEvento(Long id) {
        if (eventoRepository.buscarPorId(id) == null) {
            throw new ResourceNotFoundException("Evento não encontrado com ID: " + id);
        }

        eventoRepository.deletar(id);
        eventoRedisCache.invalidateCacheForEvento(id);
    }

    @Transactional
    public Evento atualizarEvento(Long id, Evento eventoAtualizado) {
        // 1. Busca o evento existente
        Evento eventoExistente = eventoRepository.buscarPorId(id);

        if (eventoExistente == null) {
            throw new ResourceNotFoundException("Evento não encontrado com ID: " + id);
        }

        // 2. Atualiza os dados do objeto existente
        eventoExistente.setNome(eventoAtualizado.getNome());
        eventoExistente.setDescricao(eventoAtualizado.getDescricao());
        eventoExistente.setDataInicio(eventoAtualizado.getDataInicio());
        eventoExistente.setDataFim(eventoAtualizado.getDataFim());
        eventoExistente.setLocal(eventoAtualizado.getLocal());
        eventoExistente.setCapacidadeTotal(eventoAtualizado.getCapacidadeTotal());
        eventoExistente.setStatus(eventoAtualizado.getStatus());

        // Mantém a imagem antiga se a nova não for enviada
        if (eventoAtualizado.getImagemNome() != null) {
            eventoExistente.setImagemNome(eventoAtualizado.getImagemNome());
        }

        // 3. Chama o update do JDBC
        eventoRepository.atualizar(eventoExistente);

        // 4. Atualiza Cache
        eventoRedisCache.invalidateCacheForEvento(id);
        eventoRedisCache.cacheEvento(eventoExistente);

        return eventoExistente;
    }

    @Transactional(readOnly = true)
    public Double obterReceitaTotal(Long idEvento) {
        if (eventoRepository.buscarPorId(idEvento) == null) {
            throw new ResourceNotFoundException("Evento não encontrado com ID: " + idEvento);
        }

        return eventoRepository.calcularReceitaTotal(idEvento);
    }
}