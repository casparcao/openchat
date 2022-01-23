package top.mikecao.openchat.core.registry.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.exception.AppServerException;
import top.mikecao.openchat.core.registry.Registry;
import top.mikecao.openchat.core.registry.Server;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于redis的注册表
 * @author caohailong
 */

@Slf4j
public class RedisBasedRegistry implements Registry {

    private final ObjectMapper objectMapper;
    private static final String KEY_PREFIX = "registry:server";
    private final HashOperations<String, String, String> operations;

    public RedisBasedRegistry(StringRedisTemplate redisTemplate,
                              ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
        operations = redisTemplate.opsForHash();
    }

    @Override
    public void register(long id, Server server) {
        String json ;
        try {
            json = objectMapper.writeValueAsString(server);
        } catch (JsonProcessingException e) {
            log.error("服务器注册异常>>", e);
            throw new AppServerException("服务器注册异常", e);
        }
        operations.put(KEY_PREFIX, id + "", json);
    }

    @Override
    public void unregister(long id) {
        operations.delete(KEY_PREFIX, id + "");
    }

    @Override
    public Mono<List<Server>> fetch() {
        Map<String, String> entries = operations.entries(KEY_PREFIX);
        return Mono.just(entries.values().stream()
                .map(x -> {
                    Server server;
                    try {
                        server = objectMapper.readValue(x, Server.class);
                    } catch (JsonProcessingException e) {
                        log.error("拉取服务器注册表异常>>", e);
                        throw new AppServerException("拉取服务器注册表异常>>", e);
                    }
                    return server;
                })
                .collect(Collectors.toList()));
    }
}
