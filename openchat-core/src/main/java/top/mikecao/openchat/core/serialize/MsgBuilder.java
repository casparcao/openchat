package top.mikecao.openchat.core.serialize;

import top.mikecao.openchat.core.proto.Proto;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author caohailong
 */

public final class MsgBuilder {

    private MsgBuilder(){}

    public static Proto.Message.Builder get(Proto.MsgType type) {
        return Proto.Message.newBuilder()
                .setType(type)
                .setId(ThreadLocalRandom.current().nextInt(1000))
                .setTs(System.currentTimeMillis());
    }

}
