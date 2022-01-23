package top.mikecao.openchat.core.common.generators;

import top.mikecao.openchat.core.common.Generator;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * @author caohailong
 */
public class SnowFlake implements Generator<Long> {

    private final long workerId;
    private final long datacenterId;
    private long sequence;

    public SnowFlake(long datacenter, long worker){
        this.workerId = worker;
        this.datacenterId = datacenter;
        SecureRandom random = new SecureRandom();
        byte[] temp = new byte[2];
        random.nextBytes(temp);
        BigInteger bi = new BigInteger(temp);
        this.sequence = bi.abs().intValue();

        long maxWorkerId = ~(-1L << WORKER_ID_BITS);
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("Worer应介于0-32之间>>"+workerId);
        }
        long maxDatacenterId = ~(-1L << DATACENTER_ID_BITS);
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException("Center应介于0-32之间>>"+datacenterId);
        }
    }

    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private long lastTimestamp = -1L;

    @Override
    public synchronized Long next() {
        long twepoch = 1288834974657L;
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("生成ID失败");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << TIMESTAMP_LEFT_SHIFT) |
                (datacenterId << DATACENTER_ID_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen(){
        return System.currentTimeMillis();
    }

}
