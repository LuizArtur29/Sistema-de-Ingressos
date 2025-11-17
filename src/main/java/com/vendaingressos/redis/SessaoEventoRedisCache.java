package com.vendaingressos.redis;

import com.vendaingressos.model.SessaoEvento;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
public class SessaoEventoRedisCache {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration CACHE_EXPIRATION = Duration.ofHours(1);

    public SessaoEventoRedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String buildSingleKey(Long id) {
        return "sessaoEvento:" + id;
    }

    private String buildListKeyByEvento(Long eventoPaiId) {
        return "sessoesPorEvento:" + eventoPaiId;
    }

    // --- Caching de Item Único ---

    /** Armazena o objeto SessaoEvento no cache. */
    public void cacheSessaoEvento(SessaoEvento sessaoEvento) {
        if (sessaoEvento.getIdSessao() != null) {
            String key = buildSingleKey(sessaoEvento.getIdSessao());
            redisTemplate.opsForValue().set(key, sessaoEvento, CACHE_EXPIRATION);
        }
    }

    /** Busca uma SessaoEvento no cache pelo ID. */
    public Optional<SessaoEvento> getCachedSessaoEvento(Long id) {
        String key = buildSingleKey(id);
        Object cachedObject = redisTemplate.opsForValue().get(key);

        if (cachedObject instanceof SessaoEvento) {
            return Optional.of((SessaoEvento) cachedObject);
        }
        return Optional.empty();
    }

    /** Invalida o cache de uma SessaoEvento pelo ID e as listas relacionadas. */
    public void invalidateCacheForSessaoEvento(Long id, Long eventoPaiId) {
        // Invalida o item único
        redisTemplate.delete(buildSingleKey(id));
        // Invalida a lista de sessões do evento pai
        if (eventoPaiId != null) {
            redisTemplate.delete(buildListKeyByEvento(eventoPaiId));
        }
    }

    // --- Caching de Lista por Evento Pai ---

    /** Armazena a lista de SessaoEvento no cache. */
    public void cacheSessoesPorEvento(Long eventoPaiId, List<SessaoEvento> sessoes) {
        String key = buildListKeyByEvento(eventoPaiId);
        redisTemplate.opsForValue().set(key, sessoes, CACHE_EXPIRATION);
    }

    /** Busca a lista de SessaoEvento no cache pelo ID do Evento Pai. */
    @SuppressWarnings("unchecked")
    public Optional<List<SessaoEvento>> getCachedSessoesPorEvento(Long eventoPaiId) {
        String key = buildListKeyByEvento(eventoPaiId);
        Object cachedObject = redisTemplate.opsForValue().get(key);

        if (cachedObject instanceof List) {
            // É seguro fazer o cast aqui porque Jackson garante o tipo na serialização/deserialização
            return Optional.of((List<SessaoEvento>) cachedObject);
        }
        return Optional.empty();
    }

    /** Invalida o cache da lista de Sessões por Evento Pai. */
    public void invalidateCacheForSessoesPorEvento(Long eventoPaiId) {
        redisTemplate.delete(buildListKeyByEvento(eventoPaiId));
    }
}