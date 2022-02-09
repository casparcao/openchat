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
     * @param chat 聊天记录
     */
    void store(Proto.Chat chat);

    /**
     * 存储多条聊天记录
     * @param chats 聊天记录
     */
    void store(List<Proto.Chat> chats);

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
         * @param fid 好友id
         * @param chats chats
         */
        void process(long fid, List<Proto.Chat> chats);

    }

}
