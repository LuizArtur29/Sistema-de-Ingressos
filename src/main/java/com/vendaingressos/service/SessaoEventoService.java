package com.vendaingressos.service;

import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.model.SessaoEvento;
import com.vendaingressos.redis.SessaoEventoRedisCache;
import com.vendaingressos.repository.jdbc.EventoRepository;
import com.vendaingressos.repository.jdbc.SessaoEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SessaoEventoService {

    private final SessaoEventoRepository sessaoEventoRepository;
    private final EventoRepository eventoRepository;
    private final SessaoEventoRedisCache sessaoEventoRedisCache;

    @Autowired
    public SessaoEventoService(SessaoEventoRepository sessaoEventoRepository,
                               EventoRepository eventoRepository,
                               SessaoEventoRedisCache sessaoEventoRedisCache) {
        this.sessaoEventoRepository = sessaoEventoRepository;
        this.eventoRepository = eventoRepository;
        this.sessaoEventoRedisCache = sessaoEventoRedisCache;
    }

    @Transactional
    public SessaoEvento salvarSessaoEvento(SessaoEvento sessaoEvento) {
        if (sessaoEvento.getEventoPai() == null || sessaoEvento.getEventoPai().getId() == null) {
            throw new IllegalArgumentException("O Evento Pai é obrigatório para criar uma sessão.");
        }

        Evento eventoPai = eventoRepository.buscarPorId(sessaoEvento.getEventoPai().getId());
        if (eventoPai == null) {
            throw new ResourceNotFoundException("Evento pai não encontrado com ID: " + sessaoEvento.getEventoPai().getId());
        }

        sessaoEvento.setEventoPai(eventoPai);

        sessaoEventoRepository.salvar(sessaoEvento);

        sessaoEventoRedisCache.cacheSessaoEvento(sessaoEvento);
        sessaoEventoRedisCache.invalidateCacheForSessoesPorEvento(eventoPai.getId());

        return sessaoEvento;
    }

    @Transactional(readOnly = true)
    public List<SessaoEvento> buscarTodasSessoesEventos() {
        return sessaoEventoRepository.listarTodos();
    }

    @Transactional(readOnly = true)
    public Optional<SessaoEvento> buscarSessaoEventoPorId(Long id) {
        // 1. Cache
        Optional<SessaoEvento> cachedSessao = sessaoEventoRedisCache.getCachedSessaoEvento(id);
        if (cachedSessao.isPresent()) {
            return cachedSessao;
        }

        // 2. Banco JDBC
        SessaoEvento sessao = sessaoEventoRepository.buscarPorId(id);

        // 3. Salvar no Cache se encontrou
        if (sessao != null) {
            sessaoEventoRedisCache.cacheSessaoEvento(sessao);
            return Optional.of(sessao);
        }

        return Optional.empty();
    }

    @Transactional(readOnly = true)
    public List<SessaoEvento> buscarSessoesPorEventoPai(Long eventoPaiId) {
        // 1. Cache Lista
        Optional<List<SessaoEvento>> cachedList = sessaoEventoRedisCache.getCachedSessoesPorEvento(eventoPaiId);
        if (cachedList.isPresent()) {
            return cachedList.get();
        }

        // 2. Validar se Evento Pai existe (JDBC retorna null se não existir)
        if (eventoRepository.buscarPorId(eventoPaiId) == null) {
            throw new ResourceNotFoundException("Evento pai não encontrado com ID: " + eventoPaiId);
        }

        // 3. Buscar no Banco
        List<SessaoEvento> sessoes = sessaoEventoRepository.buscarPorEventoPai(eventoPaiId);

        // 4. Cache
        if (!sessoes.isEmpty()) {
            sessaoEventoRedisCache.cacheSessoesPorEvento(eventoPaiId, sessoes);
        }
        return sessoes;
    }

    @Transactional
    public SessaoEvento atualizarSessaoEvento(Long id, SessaoEvento sessaoEventoAtualizada) {
        // 1. Busca existente
        SessaoEvento sessaoExistente = sessaoEventoRepository.buscarPorId(id);

        if (sessaoExistente == null) {
            throw new ResourceNotFoundException("Sessão de Evento não encontrada com ID: " + id);
        }

        // 2. Atualiza campos
        sessaoExistente.setNomeSessao(sessaoEventoAtualizada.getNomeSessao());
        sessaoExistente.setDataHoraSessao(sessaoEventoAtualizada.getDataHoraSessao());
        sessaoExistente.setStatusSessao(sessaoEventoAtualizada.getStatusSessao());

        // 3. Salva alteração
        sessaoEventoRepository.atualizar(sessaoExistente);

        Long eventoPaiId = sessaoExistente.getEventoPai().getId();

        // 4. Invalida caches
        sessaoEventoRedisCache.invalidateCacheForSessaoEvento(id, eventoPaiId);
        sessaoEventoRedisCache.cacheSessaoEvento(sessaoExistente);

        return sessaoExistente;
    }

    @Transactional
    public void deletarSessaoEvento(Long id) {
        SessaoEvento sessao = sessaoEventoRepository.buscarPorId(id);
        if (sessao == null) {
            throw new ResourceNotFoundException("Sessão de Evento não encontrada com ID: " + id);
        }

        Long eventoPaiId = sessao.getEventoPai().getId();

        sessaoEventoRepository.deletar(id);

        sessaoEventoRedisCache.invalidateCacheForSessaoEvento(id, eventoPaiId);
    }
}