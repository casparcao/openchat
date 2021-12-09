package top.mikecao.openchat.server;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author mike
 */
public final class Context {

    public final static ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


}
