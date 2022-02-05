package top.mikecao.openchat.client.service.store;

import top.mikecao.openchat.client.service.MsgStore;
import top.mikecao.openchat.core.proto.Proto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 基于关系型数据库的消息存储方案
 * @author caohailong
 */

public class RdbmsMsgStore implements MsgStore {

    @Override
    public void store(Proto.Chat chat, Callback... callbacks) {
        //存储

        //回调
        Stream.of(Objects.requireNonNull(callbacks, "回调函数不可为空"))
                .forEach(cb -> cb.process(Collections.singletonList(chat)));
    }

    @Override
    public void store(List<Proto.Chat> chats, Callback... callbacks) {

        Stream.of(Objects.requireNonNull(callbacks, "回调函数不可为空"))
                .forEach(cb -> cb.process(chats));
    }
}
