package top.mikecao.openchat.client.service.chat.store;

import top.mikecao.openchat.client.service.chat.ChatStore;
import top.mikecao.openchat.core.proto.Proto;

import java.util.Collections;
import java.util.List;

/**
 * 基于文件系统的简单消息存储方案
 * @author caohailong
 */

public class SimpleFsChatStore implements ChatStore {

    @Override
    public void store(boolean there, Proto.Chat chat) {
        //...
    }

    @Override
    public void store(boolean there, List<Proto.Chat> chats) {
        //...
    }

    @Override
    public void listener(Listener listener) {
        //...
    }

    @Override
    public List<Proto.Chat> load(long rid, long offset, long size) {
        return Collections.emptyList();
    }
}
