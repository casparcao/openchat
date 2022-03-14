package top.mikecao.openchat.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.serialize.MsgBuilder;

/**
 * 登录成功后，完成服务器连接后，将token发送给服务器，用于服务器端session跟踪（记录token与channel的关系）
 * @author mike
 */
@Slf4j
@ChannelHandler.Sharable
public class InitHandler extends SimpleChannelInboundHandler<Proto.Message> {

    private final String token;
    public InitHandler(String token){
        this.token = token;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //一旦通道激活，发送token给服务器
        Proto.Message message = MsgBuilder.get(Proto.MsgType.INIT)
                .setToken(token)
                .build();
        ctx.writeAndFlush(message);
    }

}
