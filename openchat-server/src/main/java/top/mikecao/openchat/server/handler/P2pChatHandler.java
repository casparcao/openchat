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
import top.mikecao.openchat.server.entity.Chat;
import top.mikecao.openchat.server.repository.SimpleChatRepository;
import top.mikecao.openchat.server.session.ChannelStore;
import top.mikecao.openchat.server.session.TokenGranter;
import top.mikecao.openchat.server.session.User;
import top.mikecao.openchat.server.session.UserLoader;

import java.util.Date;
import java.util.Optional;

/**
 * 点对点消息发送处理
 * @author mike
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class P2pChatHandler extends SimpleChannelInboundHandler<Proto.Message> {

    @Autowired
    private UserLoader userLoader;
    @Autowired
    private ChannelStore channelStore;
    @Autowired
    private TokenGranter tokenGranter;
    @Autowired
    private SimpleChatRepository simpleChatRepository;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {
        System.out.println("current thread >> " + Thread.currentThread());
        Proto.MsgType type = msg.getType();
        //不是点对点消息，不做处理
        if(!Proto.MsgType.P2P.equals(type)){
            ctx.fireChannelRead(msg);
            return;
        }
        Proto.Chat lr = msg.getChat();
        Chat chat = new Chat()
                .setId(1)
                .setMessage(lr.getMessage())
                .setBroadcast(false)
                .setType(lr.getType())
                .setFrom(lr.getFrom())
                .setTo(lr.getTo())
                .setTs(new Date());
        // 1. 将消息保存到消息库
        simpleChatRepository.save(chat)
                .subscribe();
        // 2. 将消息投递到消息队列
        // 如果对方在线，消息队列中的消息被消费时，直接投递给对方
        // 否则，消息队列消息不做处理直接抛弃，等对方上线时，重新从库中拉去未读的消息
        //ctx.writeAndFlush();
        //do not propagate
    }

    private boolean validate(User user, String password) {
        return user.getPassword().equals(password);
    }
}
