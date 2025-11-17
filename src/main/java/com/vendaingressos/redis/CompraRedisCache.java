package com.vendaingressos.redis;

import com.vendaingressos.model.Compra;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
public class CompraRedisCache {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration CACHE_EXPIRATION = Duration.ofMinutes(30); // Compras podem ter expiração menor

    public CompraRedisCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String buildSingleKey(Long id) {
        return "compra:" + id;
    }

    private String buildListKeyByUsuario(Long usuarioId) {
        return "comprasPorUsuario:" + usuarioId;
    }

    // --- Caching de Item Único (Compra por ID) ---

    /** Armazena o objeto Compra no cache. */
    public void cacheCompra(Compra compra) {
        if (compra.getIdCompra() != null) {
            String key = buildSingleKey(compra.getIdCompra());
            redisTemplate.opsForValue().set(key, compra, CACHE_EXPIRATION);
        }
    }

    /** Busca uma Compra no cache pelo ID. */
    public Optional<Compra> getCachedCompra(Long id) {
        String key = buildSingleKey(id);
        Object cachedObject = redisTemplate.opsForValue().get(key);

        if (cachedObject instanceof Compra) {
            return Optional.of((Compra) cachedObject);
        }
        return Optional.empty();
    }

    /** Invalida o cache de uma Compra pelo ID. */
    public void invalidateCacheForCompra(Long id) {
        redisTemplate.delete(buildSingleKey(id));
    }

    // --- Caching de Lista (Compras por Usuário) ---

    /** Armazena a lista de Compra por usuário no cache. */
    public void cacheComprasPorUsuario(Long usuarioId, List<Compra> compras) {
        String key = buildListKeyByUsuario(usuarioId);
        redisTemplate.opsForValue().set(key, compras, CACHE_EXPIRATION);
    }

    /** Busca a lista de Compra no cache pelo ID do Usuário. */
    @SuppressWarnings("unchecked")
    public Optional<List<Compra>> getCachedComprasPorUsuario(Long usuarioId) {
        String key = buildListKeyByUsuario(usuarioId);
        Object cachedObject = redisTemplate.opsForValue().get(key);

        // Retorna a lista desserializada, garantindo a verificação de tipo.
        if (cachedObject instanceof List) {
            return Optional.of((List<Compra>) cachedObject);
        }
        return Optional.empty();
    }

    /** Invalida o cache da lista de Compras por Usuário. */
    public void invalidateCacheForComprasPorUsuario(Long usuarioId) {
        redisTemplate.delete(buildListKeyByUsuario(usuarioId));
    }
}