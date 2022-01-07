package top.mikecao.openchat.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import top.mikecao.openchat.server.service.MaxRoomChatIdService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author caohailong
 */

@Service
public class RedisBasedMaxRoomChatIdService implements MaxRoomChatIdService {

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;
    private static final String PREFIX = "max_chat_id:";

    @Override
    public void save(long id, long max) {
        redisTemplate.opsForValue().set(PREFIX + id, max+"").subscribe();
    }

    @Override
    public Map<Long, Long> load(List<Long> ids) {
        if(Objects.isNull(ids) || ids.isEmpty()){
            return Collections.emptyMap();
        }
        List<String> params = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        return redisTemplate.opsForValue().multiGet(params)
                .map(list -> {
                    Map<Long, Long> result = new HashMap<>(32);
                    for (int i = 0; i < ids.size(); i++){
                        String max = list.get(i);
                        result.put(ids.get(i),
                                (Objects.isNull(max) ? 0L : Long.parseLong(max)));
                    }
                    return result;
                }).block();
    }
}
