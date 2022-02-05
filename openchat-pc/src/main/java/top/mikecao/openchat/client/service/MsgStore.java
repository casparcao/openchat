package top.mikecao.openchat.client.service;

import top.mikecao.openchat.core.proto.Proto;

import java.util.List;

/**
 * @author caohailong
 */

public interface MsgStore {

    /**
     * 存储一条聊天记录
     * @param chat 聊天记录
     * @param callbacks 聊天记录存储后的处理逻辑
     */
    void store(Proto.Chat chat, Callback... callbacks);

    /**
     * 存储多条聊天记录
     * @param chats 聊天记录
     * @param callbacks 聊天记录存储后的处理逻辑
     */
    void store(List<Proto.Chat> chats, Callback... callbacks);


    interface Callback{

        /**
         * 回调逻辑的处理
         * @param chats chats
         */
        void process(List<Proto.Chat> chats);

    }

}
