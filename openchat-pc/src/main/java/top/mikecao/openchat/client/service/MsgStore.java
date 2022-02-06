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
     */
    void store(Proto.Chat chat);

    /**
     * 存储多条聊天记录
     * @param chats 聊天记录
     * @param listeners 聊天记录存储后的处理逻辑
     */
    void store(List<Proto.Chat> chats);

    /**
     * 添加监听器
     * @param listener listener
     */
    void listener(Listener listener);

    /**
     * 加载指定用户{@code uid}的好友{@code fid}的聊天记录
     * @param uid 用户
     * @param fid 好友
     * @param offset 偏移量
     * @param size 条数
     * @return 聊天记录
     */
    List<Proto.Chat> load(long uid, long fid, long offset, long size);


    interface Listener {

        /**
         * 回调逻辑的处理
         * @param fid 好友id
         * @param chats chats
         */
        void process(long fid, List<Proto.Chat> chats);

    }

}
