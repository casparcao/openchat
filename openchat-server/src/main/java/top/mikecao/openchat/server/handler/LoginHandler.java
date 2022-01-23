package top.mikecao.openchat.server.handler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.auth.TokenGranter;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.session.*;

/**
 * @author mike
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class LoginHandler extends SimpleChannelInboundHandler<Proto.Message> {

    @Autowired
    private UserLoader userLoader;
    @Autowired
    private ChannelStore channelStore;
    @Autowired
    private TokenGranter tokenGranter;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {
        Proto.MsgType type = msg.getType();
        //不是登录事件，不做处理
        if(!Proto.MsgType.LOGIN.equals(type)){
            ctx.fireChannelRead(msg);
            return;
        }
    }

}
