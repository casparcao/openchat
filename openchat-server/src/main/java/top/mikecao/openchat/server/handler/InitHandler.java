package top.mikecao.openchat.server.handler;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.auth.Account;
import top.mikecao.openchat.core.auth.TokenGranter;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.session.*;

/**
 * @author mike
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class InitHandler extends SimpleChannelInboundHandler<Proto.Message> {

    @Autowired
    private ChannelStore channelStore;
    @Autowired
    private TokenGranter tokenGranter;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {
        Proto.MsgType type = msg.getType();
        //不是登录事件，不做处理
        if(!Proto.MsgType.INIT.equals(type)){
            ctx.fireChannelRead(msg);
            return;
        }
        String token = msg.getToken();
        Account account = tokenGranter.resolve(token);
        Channel channel = ctx.channel();
        channelStore.store(account.getId(), channel);
    }

}
