package top.mikecao.openchat.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.server.Context;

import java.net.InetAddress;

/**
 * 处理
 * @author mike
 */

@Component
public class TextMessageHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ctx.pipeline().get(SslHandler.class)
                .handshakeFuture().addListener(
                (GenericFutureListener<Future<Channel>>) future -> {
                    ctx.writeAndFlush(
                            "Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
                    ctx.writeAndFlush(
                            "Your session is protected by " +
                                    ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
                                    " cipher suite.\n");
                    Context.CHANNELS.add(channel);
                });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("text executor:"+Thread.currentThread());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
        super.exceptionCaught(ctx, cause);
    }
}
