package com.vendaingressos.redis;

import com.vendaingressos.model.Ingresso;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class IngressoRedisCache {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration CACHE_EXPIRATION = Duration.ofHours(1);

    public IngressoRedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String buildKey(Long id) {
        return "ingresso:" + id;
    }

    /** Armazena o objeto Ingresso no cache. */
    public void cacheIngresso(Ingresso ingresso) {
        if (ingresso.getIdIngresso() != null) {
            String key = buildKey(ingresso.getIdIngresso());
            redisTemplate.opsForValue().set(key, ingresso, CACHE_EXPIRATION);
        }
    }

    /** Busca um Ingresso no cache pelo ID. */
    public Optional<Ingresso> getCachedIngresso(Long id) {
        String key = buildKey(id);
        Object cachedObject = redisTemplate.opsForValue().get(key);

        if (cachedObject instanceof Ingresso) {
            return Optional.of((Ingresso) cachedObject);
        }
        return Optional.empty();
    }

    /** Invalida o cache de um Ingresso pelo ID. */
    public void invalidateCacheForIngresso(Long id) {
        String key = buildKey(id);
        redisTemplate.delete(key);
    }
}