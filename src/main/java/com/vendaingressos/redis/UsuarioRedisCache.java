package com.vendaingressos.redis;

import com.vendaingressos.model.Usuario;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class UsuarioRedisCache {

    // O template configurado em RedisConfig injetado
    private final RedisTemplate<String, Object> redisTemplate;
    // Chave de expiração (1 hora, como no exemplo)
    private static final Duration CACHE_EXPIRATION = Duration.ofHours(1);

    public UsuarioRedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String buildKey(Long id) {
        return "usuario:" + id;
    }

    /**
     * Armazena o objeto Usuario no cache.
     */
    public void cacheUsuario(Usuario usuario) {
        if (usuario.getIdUsuario() != null) {
            String key = buildKey(usuario.getIdUsuario());
            // .opsForValue().set(key, value, expiration, timeUnit)
            redisTemplate.opsForValue().set(key, usuario, CACHE_EXPIRATION);
        }
    }

    /**
     * Busca um Usuario no cache pelo ID.
     */
    public Optional<Usuario> getCachedUsuario(Long id) {
        String key = buildKey(id);
        Object cachedObject = redisTemplate.opsForValue().get(key);

        // Garante que o objeto retornado é de fato um Usuario antes de fazer o casting.
        // Isso é importante devido ao 'Object' do RedisTemplate.
        if (cachedObject instanceof Usuario) {
            return Optional.of((Usuario) cachedObject);
        }
        return Optional.empty();
    }

    /**
     * Invalida o cache de um Usuario pelo ID.
     */
    public void invalidateCacheForUsuario(Long id) {
        String key = buildKey(id);
        redisTemplate.delete(key);
    }
}
