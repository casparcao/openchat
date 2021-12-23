package top.mikecao.openchat.server.consumer;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.core.proto.Proto;
import top.mikecao.openchat.server.config.RabbitConfig;
import top.mikecao.openchat.server.entity.Chat;
import top.mikecao.openchat.server.repository.SimpleGroupRepository;
import top.mikecao.openchat.server.session.ChannelStore;

import java.util.Objects;
import java.util.Set;

/**
 * @author caohailong
 */

@Slf4j
@Component
public class ChatConsumer {

    @Autowired
    private ChannelStore channelStore;
    @Autowired
    private SimpleGroupRepository simpleGroupRepository;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void process(@Payload Chat chat) {
        log.info("接收到消息>>" + chat);
        //解析数据
        //判断是点对点，还是群消息
        boolean broadcast = chat.isBroadcast();
        if(broadcast){
            //群组消息
            pushGroupMessage(chat);
        }else{
            //点对点消息
            pushP2pMessage(chat.getTo(), chat);
        }
        //找到消息的接收人，查看是否在线，如果在线直接投递，否则不处理，等待上线后主动拉取
    }

    private void pushGroupMessage(Chat chat){
        simpleGroupRepository.findById(chat.getTo())
                .subscribe(group -> {
                    Set<Long> users = group.getUsers();
                    users.forEach(user -> pushP2pMessage(user, chat));
                });
    }

    private void pushP2pMessage(long uid, Chat chat){
        Channel channel = channelStore.load(uid);
        if (Objects.nonNull(channel) && channel.isActive()) {
            channel.writeAndFlush(wrap(chat));
        }
    }

    //todo 登陆后自动拉取未读消息
    private Proto.Message wrap(Chat chat){
        Proto.Chat pc = Proto.Chat.newBuilder().setType(Proto.ChatType.TEXT)
                .setTo(chat.getTo())
                .setFrom(chat.getFrom())
                .setMessage(chat.getMessage())
                .build();
        return Proto.Message.newBuilder()
                .setTs(System.currentTimeMillis())
                .setId(chat.getId())
                .setType(Proto.MsgType.P2P)
                .setChat(pc)
                .build();
    }
}
