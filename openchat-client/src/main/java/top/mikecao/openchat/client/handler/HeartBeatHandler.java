package top.mikecao.openchat.client.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import top.mikecao.openchat.client.connection.Connector;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.serialize.MsgBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author caohailong
 */

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
            if (e.state() == IdleState.READER_IDLE){
                System.out.println("client reader idle" + new Date());
                //重新连接
                this.connector.connect();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                //每隔一定时间发送一次ping消息
                try {
                    ctx.writeAndFlush(MsgBuilder.get(Proto.MsgType.PING).setToken(this.token).build()).await(5, TimeUnit.SECONDS);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                    Thread.currentThread().interrupt();
                    System.out.println("连接服务器失败>>");
                }
                System.out.println("client write idle" + new Date());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        System.out.println("client channel read" + new Date());
    }
}
