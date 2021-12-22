package top.mikecao.openchat.server.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.server.config.RabbitConfig;
import top.mikecao.openchat.server.entity.Chat;

/**
 * @author caohailong
 */

@Slf4j
@Component
public class ChatConsumer {
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void process(@Payload Chat chat) {
        log.info("接收到消息>>" + chat);
        //解析数据
        //判断是点对点，还是群消息
        //找到消息的接收人，查看是否在线，如果在线直接投递，否则不处理，等待上线后主动拉取
    }
}
