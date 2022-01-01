package top.mikecao.openchat.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.producer.MsgProducer;
import top.mikecao.openchat.server.repository.SimpleChatRepository;
import top.mikecao.openchat.server.session.TokenGranter;
import top.mikecao.openchat.server.session.User;
import top.mikecao.openchat.toolset.common.Generator;

import java.util.Date;

/**
 * 客户端连接后，查询用户的待阅读消息，并推送给客户端，
 * 该处理器在处理完成后，即可移除
 * @author mike
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class UnreadMsgFetchHandler extends SimpleChannelInboundHandler<Proto.Message> {

    @Autowired
    private SimpleChatRepository simpleChatRepository;
    @Autowired
    private MsgProducer producer;
    @Autowired
    private Generator<Long> generator;
    @Autowired
    private TokenGranter tokenGranter;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) throws Exception {

        Proto.MsgType type = msg.getType();
        //不是拉取消息，不做处理
        if(!Proto.MsgType.FETCH.equals(type)){
            ctx.fireChannelRead(msg);
            return;
        }
        String token = msg.getToken();
        User user = tokenGranter.resolve(token);
        long uid = user.getId();

        ctx.pipeline().remove(this);
    }

}
