package top.mikecao.openchat.client.service.store;

import top.mikecao.openchat.client.service.MsgStore;
import top.mikecao.openchat.core.proto.Proto;

import java.util.Collections;
import java.util.List;

/**
 * 基于文件系统的简单消息存储方案
 * @author caohailong
 */

public class SimpleFsMsgStore implements MsgStore {

    @Override
    public void store(Proto.Chat chat) {
        //...
    }

    @Override
    public void store(List<Proto.Chat> chats) {
        //...
    }

    @Override
    public void listener(Listener listener) {
        //...
    }

    @Override
    public List<Proto.Chat> load(long uid, long fid, long offset, long size) {
        return Collections.emptyList();
    }
}
