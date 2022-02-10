package top.mikecao.openchat.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.core.proto.Proto;

import java.util.concurrent.ThreadLocalRandom;


/**
 * 消息接受器，接受服务器推送的消息
 * @author mike
 */
@Slf4j
public class MsgAcceptor extends SimpleChannelInboundHandler<Proto.Message> {

    private final String token;
    public MsgAcceptor(String token){
       this.token = token;
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) throws Exception {
        log.info("ClientHandler.channelRead>>" + msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //一旦通道激活，拉取未读消息
        Proto.Message message = Proto.Message.newBuilder()
                .setId(ThreadLocalRandom.current().nextInt(1000))
                .setTs(System.currentTimeMillis())
                .setType(Proto.MsgType.PULL)
                //.setLogin(loginRequest)
                .build();
        ctx.writeAndFlush(message);
    }

}
