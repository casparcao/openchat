package top.mikecao.openchat.client.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.client.connection.Connector;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.serialize.MsgBuilder;

/**
 * @author caohailong
 */

@Slf4j
@ChannelHandler.Sharable
public class HeartBeatHandler extends ChannelDuplexHandler {

    private final Connector connector;
    private final String token;

    public HeartBeatHandler(Connector connector, String token){
        this.connector = connector;
        this.token = token;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.WRITER_IDLE) {
                //每隔一定时间发送一次ping消息
                ctx.writeAndFlush(MsgBuilder.get(Proto.MsgType.PING).setToken(this.token).build());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connector.connect();
        super.channelInactive(ctx);
    }

}
