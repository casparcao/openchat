package top.mikecao.openchat.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import top.mikecao.openchat.server.service.MaxRoomChatIdService;

import java.util.*;

/**
 * @author caohailong
 */

//@Service
public class MongoBasedMaxRoomChatIdService implements MaxRoomChatIdService {

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;
    private static final String PREFIX = "max_chat_id:";

    @Override
    public void save(long id, long max) {
        redisTemplate.opsForValue().set(PREFIX + id, max+"").subscribe();
    }

    @Override
    public Map<Long, Long> load(List<Long> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return null;
    }
}
