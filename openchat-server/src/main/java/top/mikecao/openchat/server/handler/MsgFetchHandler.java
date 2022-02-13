package top.mikecao.openchat.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.serialize.MsgBuilder;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.core.entity.User;
import top.mikecao.openchat.server.repository.SimpleUserRepository;
import top.mikecao.openchat.server.service.MaxRoomChatIdService;
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
public class MsgFetchHandler extends SimpleChannelInboundHandler<Proto.Message> {

    @Autowired
    private SimpleUserRepository simpleUserRepository;
    @Autowired
    private MaxRoomChatIdService maxRoomChatIdService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {

        ctx.fireChannelRead(msg);
        //Proto.MsgType type = msg.getType();
        //不是拉取消息，不做处理
        //if(!Proto.MsgType.PULL.equals(type)
        //        || msg.getPull().getRequest().getFull()){
        //    ctx.fireChannelRead(msg);
        //    return;
        //}
        //Account account = (Account) ctx.channel()
        //        .attr(AttributeKey.valueOf(AUTH_ATTR_NAME))
        //        .get();
        //long uid = account.getId();
        //simpleUserRepository.findById(uid)
        //        .subscribe(x -> fetch(x, ctx));
    }

    private void fetch(User user, ChannelHandlerContext ctx) {
        //Set<User.Room> rooms = user.getRooms();
        //List<Long> rids = rooms.stream()
        //        .map(User.Room::getId)
        //        .collect(Collectors.toList());
        //查询所有room的最大消息id，确认该用户在每个room中是否存在未读消息
        //Map<Long, Long> chatMaxIds = maxRoomChatIdService.load(rids);
        //Proto.Pull.Response.Builder builder = Proto.Pull.Response.newBuilder();
        //builder.addAllRooms(check(rooms, chatMaxIds));
        //
        //Proto.Pull pull = Proto.Pull.newBuilder()
        //        .setResponse(builder.build())
        //        .build();
        //Proto.Message message = MsgBuilder.get(Proto.MsgType.PULL)
        //        .setPull(pull)
        //        .build();
        //ctx.writeAndFlush(message);
    }

    /**
     * 对比已读偏移量与最大消息id，确认是否存在未读消息
     */
    //private List<Proto.Room> check(Set<User.Room> rooms,
    //                                     Map<Long, Long> maxIds){
    //    List<Proto.Room> result = new ArrayList<>();
    //    for (User.Room room: rooms){
    //        Proto.Room chat = Proto.Room.newBuilder()
    //                .setId(room.getId())
    //                .setUnread(maxIds.getOrDefault(room.getId(),0L) > room.getOffset())
    //                .build();
    //        result.add(chat);
    //    }
    //    return result;
    //}

}
