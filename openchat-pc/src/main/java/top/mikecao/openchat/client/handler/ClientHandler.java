package top.mikecao.openchat.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.core.proto.Proto;

import java.util.concurrent.ThreadLocalRandom;


/**
 * @author mike
 */
@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Proto.Message> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) throws Exception {
        log.info("ClientHandler.channelRead>>" + msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Proto.LoginRequest loginRequest = Proto.LoginRequest.newBuilder()
                .setAccount("a@b.com")
                .setPasswd("88888888")
                .build();
        Proto.Message message = Proto.Message.newBuilder()
                .setId(ThreadLocalRandom.current().nextInt(1000))
                .setTs(System.currentTimeMillis())
                .setType(Proto.MsgType.LOGIN)
                .setLogin(loginRequest)
                .build();
        System.out.println(message);

        ctx.writeAndFlush(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause);
        super.exceptionCaught(ctx, cause);
    }
}
