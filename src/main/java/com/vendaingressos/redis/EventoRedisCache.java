package com.vendaingressos.redis;

import com.vendaingressos.model.Evento;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class EventoRedisCache {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration CACHE_EXPIRATION = Duration.ofHours(1);

    public EventoRedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String buildKey(Long id) {
        return "evento:" + id;
    }

    /** Armazena o objeto Evento no cache. */
    public void cacheEvento(Evento evento) {
        if (evento.getId() != null) {
            String key = buildKey(evento.getId());
            redisTemplate.opsForValue().set(key, evento, CACHE_EXPIRATION);
        }
    }

    /** Busca um Evento no cache pelo ID. */
    public Optional<Evento> getCachedEvento(Long id) {
        String key = buildKey(id);
        Object cachedObject = redisTemplate.opsForValue().get(key);

        // Verifica o tipo para garantir a desserialização correta
        if (cachedObject instanceof Evento) {
            return Optional.of((Evento) cachedObject);
        }
        return Optional.empty();
    }

    /** Invalida o cache de um Evento pelo ID. */
    public void invalidateCacheForEvento(Long id) {
        String key = buildKey(id);
        redisTemplate.delete(key);
    }
}