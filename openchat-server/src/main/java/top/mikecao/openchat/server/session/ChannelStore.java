package top.mikecao.openchat.server.session;

import io.netty.channel.Channel;

/**
 * @author caohailong
 */

public interface ChannelStore {

    /**
     * 保存用户id与建立连接的关系
     * @param uid uid
     * @param channel channel
     */
    void store(long uid, Channel channel);

    /**
     * 查询指定用户id所建立的连接
     * @param uid uid
     * @return channel
     */
    Channel load(long uid);

    /**
     * 进行消息的广播, 给所有channel发送消息
     * @param message  message
     */
    void broadcast(Object message);

}