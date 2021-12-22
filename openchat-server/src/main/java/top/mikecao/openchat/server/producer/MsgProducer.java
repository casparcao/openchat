package top.mikecao.openchat.server.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import top.mikecao.openchat.server.config.RabbitConfig;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author caohailong
 */

@Slf4j
@Component
public class MsgProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void produce(String id, Object message){
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY,
                message,
                new CorrelationData(id));
    }

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback((CorrelationData correlation, boolean ack, @Nullable String cause) -> {
            //消息投递失败
            if(!ack){
                log.error("消息发送失败>>" + correlation + "；失败原因>>" + cause);
            }
        });
    }

    @PreDestroy
    public void destroy(){
        rabbitTemplate.destroy();
    }
}
