package top.mikecao.openchat.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import top.mikecao.openchat.toolset.common.Generator;
import top.mikecao.openchat.toolset.common.generators.SnowFlake;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author caohailong
 */

@Configuration
@ConfigurationProperties(prefix = "spring.application")
public class IdConfig {

    Logger logger  = LoggerFactory.getLogger(IdConfig.class);

    private String name;

    private static final int DATA_SIZE = 32;
    private static final String[] RADIX_STR = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
            "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v"};
    private static final Map<String, Integer> RADIX_MAP = new LinkedHashMap<>();

    static {
        for (int i = 0; i < DATA_SIZE; i++) {
            RADIX_MAP.put(RADIX_STR[i], i);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 计算雪花算法参数
     */
    @Bean
    public Generator<Long> generator(StringRedisTemplate redisTemplate){
        //String key = "generator:" + name;
        //RedisAtomicLong redisAtomicLong = new RedisAtomicLong(key,
        //        Objects.requireNonNull(redisTemplate.getConnectionFactory()));
        //long idIncrement = redisAtomicLong.getAndIncrement();
        //rangeId在0~1023之间
        //long rangeId = idIncrement & (DATA_SIZE*DATA_SIZE-1);
        //将rangeId转化为32进制数，十位为datacenter 个位为workerId，
        //String rangeStr = Integer.toString(Math.toIntExact(rangeId), DATA_SIZE);
        //不足两位左补零
        //String result = StringUtils.leftPad(rangeStr, 2, "0");
        //String centerStr = result.substring(0, 1);
        //String workerStr = result.substring(1, 2);
        //int datacenter = RADIX_MAP.get(centerStr);
        //int workerId = RADIX_MAP.get(workerStr);
        int datacenter = 1;
        int workerId = 1;
        logger.info(">>>>datacenter:{}=====workerId:{}",datacenter,workerId);
        return new SnowFlake(datacenter,workerId);
    }
}
