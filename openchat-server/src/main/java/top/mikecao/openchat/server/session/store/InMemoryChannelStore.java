package top.mikecao.openchat.server.session.store;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.server.session.ChannelStore;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author caohailong
 */

@Component
public class InMemoryChannelStore implements ChannelStore {

    private static final ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final ConcurrentMap<Long, ChannelId> TOKEN_CHANNEL_ID_MAP = new ConcurrentHashMap<>();

    @Override
    public void store(long uid, Channel channel){
        CHANNELS.add(channel);
        ChannelId id = channel.id();
        TOKEN_CHANNEL_ID_MAP.put(uid, id);
    }

    @Override
    public Channel load(long uid){
        ChannelId cid = TOKEN_CHANNEL_ID_MAP.get(uid);
        if (Objects.isNull(cid)) {
            return null;
        }
        return CHANNELS.find(cid);
    }

    @Override
    public void broadcast(Object message) {
        CHANNELS.writeAndFlush(message);
    }
}
