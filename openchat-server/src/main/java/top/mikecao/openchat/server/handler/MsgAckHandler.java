package top.mikecao.openchat.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.entity.User;
import top.mikecao.openchat.server.producer.MsgProducer;
import top.mikecao.openchat.server.repository.SimpleChatRepository;
import top.mikecao.openchat.server.repository.SimpleUserRepository;
import top.mikecao.openchat.server.session.Auth;
import top.mikecao.openchat.server.session.TokenGranter;
import top.mikecao.openchat.toolset.common.Generator;

import java.util.Set;

import static top.mikecao.openchat.server.filter.AuthFilter.AUTH_ATTR_NAME;

/**
 * 标记客户端读取了那条记录
 * <p>如果是点对点消息，则标记user.friends.offset的值为当前已读消息的id</p>
 * <p>如果是群组消息，则标记user.groups.offset的值为当前已读消息的id</p>
 * @author mike
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class MsgAckHandler extends SimpleChannelInboundHandler<Proto.Message> {

    @Autowired
    private SimpleChatRepository simpleChatRepository;
    @Autowired
    private SimpleUserRepository simpleUserRepository;
    @Autowired
    private MsgProducer producer;
    @Autowired
    private Generator<Long> generator;
    @Autowired
    private TokenGranter tokenGranter;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) throws Exception {

        Proto.MsgType type = msg.getType();
        //不是已读标记消息，不做处理
        if(!Proto.MsgType.READ.equals(type)){
            ctx.fireChannelRead(msg);
            return;
        }
        Auth auth = (Auth) ctx.channel()
                .attr(AttributeKey.valueOf(AUTH_ATTR_NAME))
                .get();
        long uid = auth.getId();

        simpleUserRepository.findById(uid)
                .subscribe(x -> fetch(x, ctx));
    }

    private void fetch(User user, ChannelHandlerContext ctx) {
        Set<User.Group> groups = user.getGroups();
        //查询所有群组消息中id
        Set<User.Friend> friends = user.getFriends();

    }

}
