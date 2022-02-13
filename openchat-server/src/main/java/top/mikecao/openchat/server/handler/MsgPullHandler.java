package top.mikecao.openchat.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.serialize.MsgBuilder;
import top.mikecao.openchat.core.exception.AppClientException;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.entity.Chat;
import top.mikecao.openchat.core.entity.User;
import top.mikecao.openchat.server.repository.SimpleChatRepository;
import top.mikecao.openchat.server.repository.SimpleUserRepository;
import top.mikecao.openchat.core.auth.Account;

import java.util.*;
import java.util.stream.Collectors;

import static top.mikecao.openchat.server.filter.AuthFilter.AUTH_ATTR_NAME;

/**
 * 客户端连接后，查询用户是否有未读的消息
 * @author mike
 */

@Deprecated(since = "2022-02-13", forRemoval = true)
@Slf4j
@Component
@ChannelHandler.Sharable
public class MsgPullHandler extends SimpleChannelInboundHandler<Proto.Message> {

    @Autowired
    private SimpleUserRepository simpleUserRepository;
    @Autowired
    private SimpleChatRepository simpleChatRepository;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {

        ctx.fireChannelRead(msg);
        //Proto.MsgType type = msg.getType();
        //不是拉取消息，不做处理
        //if(!Proto.MsgType.PULL.equals(type)
        //        || !msg.getPull().getRequest().getFull()){
        //    ctx.fireChannelRead(msg);
        //    return;
        //}
        //Account account = (Account) ctx.channel()
        //        .attr(AttributeKey.valueOf(AUTH_ATTR_NAME))
        //        .get();
        //Proto.Pull.Request request = msg.getPull().getRequest();
        //待拉取消息的聊天室id列表
        //出于性能考虑，每次只拉取一个聊天室的消息
        //Long rid = request.getRoom();
        //long uid = account.getId();
        //simpleUserRepository.findById(uid)
        //        .subscribe(x -> pull(x, rid, ctx));
    }

    private void pull(User user, Long rid, ChannelHandlerContext ctx) {
        //Set<User.Room> rooms = user.getRooms();
        //User.Room ur = rooms.stream()
        //        .filter(x -> rid == x.getId())
        //        .findAny()
        //        .orElseThrow(() -> new AppClientException("不存在的聊天室"));
        Proto.Pull.Response.Builder builder = Proto.Pull.Response.newBuilder();
        //builder.addAllRooms(pull(ur));

        Proto.Pull pull = Proto.Pull.newBuilder()
                .setResponse(builder.build())
                .build();
        Proto.Message message = MsgBuilder.get(Proto.MsgType.PULL)
                .setPull(pull)
                .build();
        ctx.writeAndFlush(message);
    }

    /**
     * 对比已读偏移量与最大消息id，确认是否存在未读消息
     */
    //private List<Proto.Room> pull(User.Room ur){
    //    List<Proto.Room> result = new ArrayList<>();
    //    List<Chat> chats = simpleChatRepository.findByRoomAndIdGreaterThan(ur.getId(), ur.getOffset());
    //    Proto.Room room = Proto.Room.newBuilder()
    //            .setId(ur.getId())
    //            .addAllChats(convert(chats))
    //            .build();
    //    result.add(room);
    //    return result;
    //}

    private List<Proto.Chat> convert(List<Chat> chats) {
        return chats.stream()
                .map(chat -> {
                    Proto.Chat.Builder builder = Proto.Chat.newBuilder();
                    return builder.setSpeaker(chat.getSpeaker())
                            .setNickname(chat.getNickname())
                            .setType(chat.getType())
                            .setMessage(chat.getMessage())
                            .setRoom(chat.getRoom())
                            .build();
                })
                .collect(Collectors.toList());
    }

}
