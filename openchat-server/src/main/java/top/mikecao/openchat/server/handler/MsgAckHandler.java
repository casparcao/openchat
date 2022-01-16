package top.mikecao.openchat.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.repository.MgmtUserRepository;
import top.mikecao.openchat.core.auth.Account;

import static top.mikecao.openchat.server.filter.AuthFilter.AUTH_ATTR_NAME;

/**
 * 标记客户端读取了那条记录
 * @author mike
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class MsgAckHandler extends SimpleChannelInboundHandler<Proto.Message> {

    @Autowired
    private MgmtUserRepository mgmtUserRepository;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {

        Proto.MsgType type = msg.getType();
        //不是消息确认，不做处理
        if(!Proto.MsgType.ACK.equals(type)){
            ctx.fireChannelRead(msg);
            return;
        }
        Account account = (Account) ctx.channel()
                .attr(AttributeKey.valueOf(AUTH_ATTR_NAME))
                .get();
        long uid = account.getId();
        //确认消息，每次更新用户已读消息的最大id，offset
        Proto.Ack ack = msg.getAck();

        mgmtUserRepository.ack(uid, ack);
    }

}
