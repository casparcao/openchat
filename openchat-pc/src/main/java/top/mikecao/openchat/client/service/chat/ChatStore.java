package top.mikecao.openchat.client.service.chat;

import top.mikecao.openchat.core.proto.Proto;

import java.util.List;

/**
 * 消息存储服务
 * @author caohailong
 */

public interface ChatStore {

    /**
     * 存储一条聊天记录
     * @param there 是否是对方发送的消息
     * @param chat 聊天记录
     */
    void store(boolean there, Proto.Chat chat);

    /**
     * 存储多条聊天记录
     * @param there 是否是对方发送的消息
     * @param chats 聊天记录
     */
    void store(boolean there, List<Proto.Chat> chats);

    /**
     * 添加监听器
     * @param listener listener
     */
    void listener(Listener listener);

    /**
     * 加载指定聊天室{@code rid}的聊天记录
     * @param rid 聊天室
     * @param offset 偏移量
     * @param size 条数
     * @return 聊天记录
     */
    List<Proto.Chat> load(long rid, long offset, long size);


    interface Listener {

        /**
         * 回调逻辑的处理
         * @param there 是否是对方的消息
         * @param rid 聊天室id
         * @param chats chats
         */
        void process(boolean there, long rid, List<Proto.Chat> chats);

    }

}
