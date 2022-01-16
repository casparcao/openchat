package top.mikecao.openchat.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import top.mikecao.openchat.core.registry.Registry;
import top.mikecao.openchat.core.registry.provider.RedisBasedRegistry;

/**
 * @author caohailong
 */

@Configuration
public class RegistryConfig {

    @Bean
    public Registry serverRegistry(
            ReactiveStringRedisTemplate redisTemplate,
            ObjectMapper objectMapper
    ){
        return new RedisBasedRegistry(redisTemplate, objectMapper);
    }
}
