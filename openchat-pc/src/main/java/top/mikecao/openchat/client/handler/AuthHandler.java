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
public class AuthHandler extends SimpleChannelInboundHandler<Proto.Message> {

    private final String token;
    public AuthHandler(String token){
       this.token = token;
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) throws Exception {
        log.info("ClientHandler.channelRead>>" + msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //一旦通道激活，发送当前用户token进行验证
        //验证失败后，token过期，关闭通道，并执行回调，重新登录获取最新token
            Proto.Message message = Proto.Message.newBuilder()
                    .setId(ThreadLocalRandom.current().nextInt(1000))
                    .setTs(System.currentTimeMillis())
                    .setType(Proto.MsgType.LOGIN)
                    //.setLogin(loginRequest)
                    .build();
            ctx.writeAndFlush(message);
    }

}
