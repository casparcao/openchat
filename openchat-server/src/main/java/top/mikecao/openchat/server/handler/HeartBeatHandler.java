package top.mikecao.openchat.server.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.serialize.MsgBuilder;

import java.util.Date;

/**
 * @author caohailong
 */

@Component
@ChannelHandler.Sharable
public class HeartBeatHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                //如果一段时间内没有收到客户端消息，则认为连接已断开
                System.out.println("server reader idle>>" + new Date());
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                //每隔一定时间发送一次ping消息
                System.out.println("server write idle" + new Date());
                ctx.writeAndFlush(MsgBuilder.get(Proto.MsgType.INIT).build());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server channel read>>" + new Date());
        super.channelRead(ctx, msg);
    }
}
