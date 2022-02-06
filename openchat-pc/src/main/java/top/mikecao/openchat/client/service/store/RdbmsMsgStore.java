package top.mikecao.openchat.client.service.store;

import top.mikecao.openchat.client.service.MsgStore;
import top.mikecao.openchat.core.proto.Proto;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 基于关系型数据库的消息存储方案
 * @author caohailong
 */

public class RdbmsMsgStore implements MsgStore {

    private final List<Listener> listeners = new LinkedList<>();

    @Override
    public void store(Proto.Chat chat) {
        //存储

        //回调
        Objects.requireNonNull(listeners, "回调函数不可为空")
                .forEach(cb -> cb.process(chat.getRoom(), Collections.singletonList(chat)));
    }

    @Override
    public void store(List<Proto.Chat> chats) {

        Objects.requireNonNull(listeners, "回调函数不可为空")
                .forEach(cb -> cb.process(chats.get(0).getRoom(), chats));
    }

    @Override
    public void listener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public List<Proto.Chat> load(long uid, long fid, long offset, long size) {
        return Collections.emptyList();
    }
}
