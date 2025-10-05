package org.utn.ba.tptacsg2.services;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class RedisCacheService {
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> reservarCupoScript;

    public RedisCacheService(StringRedisTemplate redisTemplate, DefaultRedisScript<Long> reservarCupoScript) {
        this.redisTemplate = redisTemplate;
        this.reservarCupoScript = reservarCupoScript;
    }

    public void put(String key, String value, Duration ttl) {
        if (ttl != null) {
            redisTemplate.opsForValue().set(key, value, ttl);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    public void crearEventoConCupos(String id, Integer cupoMaximo) {
        redisTemplate.opsForValue().set(eventoKey(id), Integer.toString(cupoMaximo));
    }

    public boolean reservarCupo(String eventoId) {
        Long r = redisTemplate.execute(reservarCupoScript, List.of(eventoKey(eventoId)));
        if (r == null) throw new IllegalStateException("Error ejecutando script de Redis");
        return r == 1L;
    }

    private String eventoKey(String eventId) { return "evento:" + eventId + ":cuposDisponibles"; }

    public Optional<String> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    public boolean ping() {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisConnection connection) -> {
            String result = connection.ping();
            return "PONG".equalsIgnoreCase(result);
        }));
    }
}
