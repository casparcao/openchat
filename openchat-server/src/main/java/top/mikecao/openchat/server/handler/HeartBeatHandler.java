package top.mikecao.openchat.server.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author caohailong
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class HeartBeatHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                //如果一段时间内没有收到客户端消息，则认为连接已断开
                log.info("长时间未检测到客户端的心跳，主动断开连接>>" + ctx.channel().id().asLongText());
                ctx.close();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("连接断开>>" + ctx.channel().id().asLongText());
        super.channelInactive(ctx);
    }

}
