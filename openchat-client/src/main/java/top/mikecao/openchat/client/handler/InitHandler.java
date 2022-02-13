package top.mikecao.openchat.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.core.proto.Proto;

/**
 * 登录成功后，完成服务器连接后，将token发送给服务器，用于session跟踪
 * @author mike
 */
@Slf4j
public class InitHandler extends SimpleChannelInboundHandler<Proto.Message> {

    private String token;

    public void token(String token){
        this.token = token;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //一旦通道激活，发送token给服务器
        Proto.Message message = Proto.Message.newBuilder()
                .setTs(System.currentTimeMillis())
                .setType(Proto.MsgType.INIT)
                .setToken(token)
                .build();
        ctx.writeAndFlush(message);
    }

}
