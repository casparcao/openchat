package top.mikecao.openchat.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.entity.Chat;
import top.mikecao.openchat.server.producer.MsgProducer;
import top.mikecao.openchat.server.repository.SimpleChatRepository;
import top.mikecao.openchat.server.service.MaxRoomChatIdService;
import top.mikecao.openchat.toolset.common.Generator;

import java.util.Date;

/**
 * 点对点消息发送处理
 * @author mike
 */

@Slf4j
@Component
@ChannelHandler.Sharable
public class MsgSendHandler extends SimpleChannelInboundHandler<Proto.Message> {

    @Autowired
    private SimpleChatRepository simpleChatRepository;
    @Autowired
    private MsgProducer producer;
    @Autowired
    private Generator<Long> generator;
    @Autowired
    private MaxRoomChatIdService maxRoomChatIdService;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Proto.Message msg) {
        Proto.MsgType type = msg.getType();
        //不是点对点消息，不做处理
        if(!Proto.MsgType.SEND.equals(type)){
            ctx.fireChannelRead(msg);
            return;
        }
        long id = generator.next();
        Proto.Chat pc = msg.getChat();
        Chat chat = new Chat()
                .setId(id)
                .setMessage(pc.getMessage())
                .setType(pc.getType())
                .setSpeaker(pc.getSpeaker())
                .setRoom(pc.getRoom())
                .setTs(new Date());
        // 1. 将消息保存到消息库
        simpleChatRepository.save(chat)
                .subscribe();
        // 2. 保存房间消息id最大值
        maxRoomChatIdService.save(pc.getRoom(), id);
        // 3. 将消息投递到消息队列
        producer.produce(id + "", chat);
        // 如果对方在线，消息队列中的消息被消费时，直接投递给对方
        // 否则，消息队列消息不做处理直接抛弃，等对方上线时，重新从库中拉去未读的消息
        //do not propagate
    }

}
