package org.utn.ba.tptacsg2.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisScriptConfig {

    /**
     * Script Lua que descuenta 1 cupo solo si stock > 0.
     * KEYS[1] = "event:{id}:stock"
     * return 1 -> reservado OK
     * return 0 -> sin cupos
     */
    @Bean
    public DefaultRedisScript<Long> reservarCupoScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);

        String lua = """
      local stock = tonumber(redis.call('GET', KEYS[1]) or '0')
      if stock > 0 then
        redis.call('DECR', KEYS[1])
        return 1
      else
        return 0
      end
      """;

        script.setScriptText(lua);
        return script;
    }
}
