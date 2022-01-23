package top.mikecao.openchat.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.mikecao.openchat.core.registry.Registry;
import top.mikecao.openchat.core.registry.provider.RedisBasedRegistry;

/**
 * @author caohailong
 */

@Configuration
public class RegistryConfig {

    @Bean
    public Registry serverRegistry(
            StringRedisTemplate redisTemplate2,
            ObjectMapper objectMapper
    ){
        return new RedisBasedRegistry(redisTemplate2, objectMapper);
    }
}
