package top.mikecao.openchat.server.consumer;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.serialize.MsgBuilder;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.config.RabbitConfig;
import top.mikecao.openchat.core.entity.Chat;
import top.mikecao.openchat.server.repository.SimpleRelationRepository;
import top.mikecao.openchat.server.session.ChannelStore;

import java.util.Objects;

/**
 * @author caohailong
 */

@Slf4j
@Component
public class ChatConsumer {

    @Autowired
    private ChannelStore channelStore;
    @Autowired
    private SimpleRelationRepository simpleRelationRepository;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void process(@Payload Chat chat) {
        log.info("接收到消息>>" + chat);
        //解析数据
        push(chat);
        //找到消息的接收人，查看是否在线，如果在线直接投递，否则不处理，等待上线后主动拉取
    }

    private void push(Chat chat){
        simpleRelationRepository.findByRid(chat.getRoom())
                .subscribe(relation -> push0(relation.getUid(), chat));
    }

    private void push0(long uid, Chat chat){
        Channel channel = channelStore.load(uid);
        if (Objects.nonNull(channel) && channel.isActive()) {
            log.info("用户在线，推送消息>>" + uid);
            channel.writeAndFlush(wrap(chat));
        }
    }

    private Proto.Message wrap(Chat chat){
        Proto.Chat pc = Proto.Chat.newBuilder()
                .setType(chat.getType())
                .setRoom(chat.getRoom())
                .setSpeaker(chat.getSpeaker())
                .setMessage(chat.getMessage())
                .build();
        Proto.Push push = Proto.Push.newBuilder()
                .setChat(pc)
                .build();
        return MsgBuilder.get(Proto.MsgType.PUSH)
                .setPush(push)
                .build();
    }
}
