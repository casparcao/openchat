package top.mikecao.openchat.server.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author caohailong
 */

@Slf4j
@Component
public class ChatConsumer {
    @RabbitListener(queues = "chat_queue")
    public String processMessage1(String msg) {
        System.out.println(Thread.currentThread().getName() + " 接收到来自hello.queue1队列的消息：" + msg);
        return msg.toUpperCase();
    }
}
