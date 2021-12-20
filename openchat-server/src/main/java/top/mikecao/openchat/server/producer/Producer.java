package top.mikecao.openchat.server.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author caohailong
 */

@Component
public class Producer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(){
        //rabbitTemplate.convertAndSend();
    }
}
