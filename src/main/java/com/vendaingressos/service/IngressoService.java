package com.vendaingressos.service;

import com.vendaingressos.exception.BadRequestException;
import com.vendaingressos.exception.ResourceNotFoundException;
import com.vendaingressos.model.Ingresso;
import com.vendaingressos.model.SessaoEvento;
import com.vendaingressos.redis.IngressoRedisCache;
import com.vendaingressos.repository.jdbc.IngressoRepository;
import com.vendaingressos.repository.jdbc.SessaoEventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final SessaoEventoRepository sessaoEventoRepository;
    private final IngressoRedisCache ingressoRedisCache;

    @Autowired
    public IngressoService(IngressoRepository ingressoRepository, SessaoEventoRepository sessaoEventoRepository, IngressoRedisCache ingressoRedisCache) {
        this.ingressoRepository = ingressoRepository;
        this.sessaoEventoRepository = sessaoEventoRepository;
        this.ingressoRedisCache = ingressoRedisCache;
    }

    @Transactional
    public Ingresso criarIngressoParaSessaoEvento(Long sessaoEventoId, Ingresso ingresso) {
        SessaoEvento sessaoEvento = sessaoEventoRepository.buscarPorId(sessaoEventoId);
        if (sessaoEvento == null) {
            throw new ResourceNotFoundException("Sessão de Evento não encontrada com ID: " + sessaoEventoId);
        }

        if (sessaoEvento.getEventoPai() == null) {
            throw new RuntimeException("Erro interno: Dados do evento pai não carregados para a sessão.");
        }

        Integer capacidadeTotalEvento = sessaoEvento.getEventoPai().getCapacidadeTotal();
        long ingressosExistentesNestaSessao = ingressoRepository.contarIngressosPorSessao(sessaoEventoId);

        if (ingressosExistentesNestaSessao >= capacidadeTotalEvento) {
            throw new BadRequestException("A sessão do evento atingiu sua capacidade máxima de ingressos.");
        }

        ingresso.setSessaoEvento(sessaoEvento);
        ingressoRepository.salvar(ingresso);

        ingressoRedisCache.cacheIngresso(ingresso);

        return ingresso;
    }

    @Transactional(readOnly = true)
    public List<Ingresso> listarIngressosPorSessaoEvento(Long sessaoEventoId) {
        if (sessaoEventoRepository.buscarPorId(sessaoEventoId) == null) {
            throw new ResourceNotFoundException("Sessão de Evento não encontrada com ID: " + sessaoEventoId);
        }

        // Otimização: Busca direta no banco filtrando por ID, sem stream no Java
        return ingressoRepository.buscarPorSessao(sessaoEventoId);
    }


    @Transactional(readOnly = true)
    public Optional<Ingresso> buscarIngressoPorId(Long ingressoId) {
        // 1. Tenta buscar no cache
        Optional<Ingresso> cachedIngresso = ingressoRedisCache.getCachedIngresso(ingressoId);
        if (cachedIngresso.isPresent()) {
            return cachedIngresso;
        }

        // 2. Se não estiver no cache, busca no banco
        Ingresso ingresso = ingressoRepository.buscarPorId(ingressoId);

        // 3. Se encontrado, armazena no cache antes de retornar
        if (ingresso != null) {
            ingressoRedisCache.cacheIngresso(ingresso);
            return Optional.of(ingresso);
        }

        return Optional.empty();
    }

    @Transactional
    public boolean isIngressoValido(Long ingressoId) {
        Optional<Ingresso> ingressoOpt = buscarIngressoPorId(ingressoId);
        if (ingressoOpt.isEmpty()) {
            throw new ResourceNotFoundException("Ingresso não encontrado");
        }
        return ingressoOpt.get().isIngressoDisponivel();
    }

    @Transactional
    public void registrarEntrada(Long ingressoId) {
        Ingresso ingresso = ingressoRepository.buscarPorId(ingressoId);

        if (ingresso == null) {
            throw new ResourceNotFoundException("Ingresso não encontrado");
        }

        if (!ingresso.isIngressoDisponivel()) {
            throw new BadRequestException("Ingresso já utilizado ou não disponível.");
        }

        // Atualiza estado
        ingresso.setIngressoDisponivel(false);

        // IMPORTANTE: No JDBC precisamos mandar atualizar explicitamente
        ingressoRepository.atualizar(ingresso);

        // Invalida e recria o cache
        ingressoRedisCache.invalidateCacheForIngresso(ingressoId);
        ingressoRedisCache.cacheIngresso(ingresso);
    }
}