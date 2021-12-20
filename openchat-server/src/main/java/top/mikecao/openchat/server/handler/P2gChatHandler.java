package top.mikecao.openchat.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.MsgBuilder;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.session.ChannelStore;
import top.mikecao.openchat.server.session.TokenGranter;
import top.mikecao.openchat.server.session.User;
import top.mikecao.openchat.server.session.UserLoader;

import java.util.Optional;

/**
 * 群组消息发送处理
 * @author mike
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class P2gChatHandler extends SimpleChannelInboundHandler<Proto.Message> {

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
        Proto.LoginRequest lr = msg.getLogin();
        String account = lr.getAccount();
        String password = lr.getPasswd();
    }

    private boolean validate(User user, String password) {
        return user.getPassword().equals(password);
    }
}
