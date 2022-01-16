package top.mikecao.openchat.core.registry.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;
import top.mikecao.openchat.core.exception.AppServerException;
import top.mikecao.openchat.core.registry.Registry;
import top.mikecao.openchat.core.registry.Server;

import java.util.List;

/**
 * 基于redis的注册表
 * @author caohailong
 */

@Slf4j
public class RedisBasedRegistry implements Registry {

    private final ObjectMapper objectMapper;
    private static final String KEY_PREFIX = "registry:server";
    private final ReactiveHashOperations<String, String, String> operations;

    public RedisBasedRegistry(ReactiveStringRedisTemplate redisTemplate,
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
        operations.put(KEY_PREFIX, id + "", json).subscribe();
    }

    @Override
    public void unregister(long id) {
        operations.remove(KEY_PREFIX, id + "").subscribe();
    }

    @Override
    public Mono<List<Server>> fetch() {
        return operations.entries(KEY_PREFIX)
                .map(x -> {
                    Server server;
                    try {
                        server = objectMapper.readValue(x.getValue(), Server.class);
                    } catch (JsonProcessingException e) {
                        log.error("拉取服务器注册表异常>>", e);
                        throw new AppServerException("拉取服务器注册表异常>>", e);
                    }
                    return server;
                })
                .collectList();
    }
}
