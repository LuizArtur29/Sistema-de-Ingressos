package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException; // Se precisar de validações
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Evento;
import com.vendaingressos.model.SessaoEvento;
import com.vendaingressos.redis.SessaoEventoRedisCache;
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
    private final SessaoEventoRedisCache sessaoEventoRedisCache;

    @Autowired
    public SessaoEventoService(SessaoEventoRepository sessaoEventoRepository, EventoRepository eventoRepository, SessaoEventoRedisCache sessaoEventoRedisCache) {
        this.sessaoEventoRepository = sessaoEventoRepository;
        this.eventoRepository = eventoRepository;
        this.sessaoEventoRedisCache = sessaoEventoRedisCache;
    }

    @Transactional
    public SessaoEvento salvarSessaoEvento(SessaoEvento sessaoEvento) {
        // Validação: garantir que o evento pai exista
        Evento eventoPai = eventoRepository.findById(sessaoEvento.getEventoPai().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento pai não encontrado com ID: " + sessaoEvento.getEventoPai().getId()));

        sessaoEvento.setEventoPai(eventoPai);
        SessaoEvento sessaoSalva = sessaoEventoRepository.save(sessaoEvento);

        sessaoEventoRedisCache.cacheSessaoEvento(sessaoSalva);
        sessaoEventoRedisCache.invalidateCacheForSessoesPorEvento(eventoPai.getId());

        return sessaoSalva;
    }

    @Transactional(readOnly = true)
    public List<SessaoEvento> buscarTodasSessoesEventos() {
        return sessaoEventoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<SessaoEvento> buscarSessaoEventoPorId(Long id) {
        // 1. Tenta buscar no cache
        Optional<SessaoEvento> cachedSessao = sessaoEventoRedisCache.getCachedSessaoEvento(id);
        if (cachedSessao.isPresent()) {
            return cachedSessao;
        }

        // 2. Se não estiver no cache, busca no banco
        Optional<SessaoEvento> dbSessao = sessaoEventoRepository.findById(id);

        // 3. Se encontrado, armazena no cache antes de retornar
        dbSessao.ifPresent(sessaoEventoRedisCache::cacheSessaoEvento);

        return dbSessao;
    }

    @Transactional(readOnly = true)
    public List<SessaoEvento> buscarSessoesPorEventoPai(Long eventoPaiId) {
        // Opcional: verificar se o EventoPai existe antes de buscar as sessões
        // 1. Tenta buscar a lista no cache
        Optional<List<SessaoEvento>> cachedList = sessaoEventoRedisCache.getCachedSessoesPorEvento(eventoPaiId);
        if (cachedList.isPresent()) {
            return cachedList.get();
        }

        // 2. Verifica a existência do evento pai e busca no banco
        if (!eventoRepository.existsById(eventoPaiId)) {
            throw new ResourceNotFoundException("Evento pai não encontrado com ID: " + eventoPaiId);
        }
        List<SessaoEvento> sessoes = sessaoEventoRepository.findByEventoPaiId(eventoPaiId);

        // 3. Se encontrado, armazena a lista no cache
        if (!sessoes.isEmpty()) {
            sessaoEventoRedisCache.cacheSessoesPorEvento(eventoPaiId, sessoes);
        }
        return sessoes;
    }

    @Transactional
    public SessaoEvento atualizarSessaoEvento(Long id, SessaoEvento sessaoEventoAtualizada) {
        return sessaoEventoRepository.findById(id).map(sessao -> {
            sessao.setNomeSessao(sessaoEventoAtualizada.getNomeSessao());
            sessao.setDataHoraSessao(sessaoEventoAtualizada.getDataHoraSessao());
            sessao.setStatusSessao(sessaoEventoAtualizada.getStatusSessao());
            // Se o eventoPai puder ser atualizado, adicione lógica para buscar e setar o novo eventoPai
            // Por simplicidade, assumimos que o eventoPai não muda após a criação.

            Long eventoPaiId = sessao.getEventoPai().getId();

            SessaoEvento sessaoSalva = sessaoEventoRepository.save(sessao);

            // Invalida e recria o cache do item único, e invalida a lista
            sessaoEventoRedisCache.invalidateCacheForSessaoEvento(id, eventoPaiId);
            sessaoEventoRedisCache.cacheSessaoEvento(sessaoSalva);

            return sessaoSalva;
        }).orElseThrow(() -> new ResourceNotFoundException("Sessão de Evento não encontrada com ID: " + id));
    }

    @Transactional
    public void deletarSessaoEvento(Long id) {
        SessaoEvento sessao = sessaoEventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão de Evento não encontrada com ID: " + id));

        Long eventoPaiId = sessao.getEventoPai().getId();

        sessaoEventoRepository.deleteById(id);

        // Invalida o item único e a lista
        sessaoEventoRedisCache.invalidateCacheForSessaoEvento(id, eventoPaiId);
    }
}