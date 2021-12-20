package top.mikecao.openchat.server.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author caohailong
 */

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange chatTopicExchange(){
        return new TopicExchange("chat_topic_exchange", true, false);
    }

    @Bean
    public Queue chatQueue(){
        return new Queue("chat_queue", true, false, false);
    }

    @Bean
    public Binding bindingChatTopicExchangeAndQueue(){
        return BindingBuilder.bind(chatQueue()).to(chatTopicExchange()).with("chat");
    }

    @Autowired
    private AmqpAdmin admin;
    @PostConstruct
    public void init(){
        admin.declareExchange(chatTopicExchange());
        admin.declareQueue(chatQueue());
        admin.declareBinding(bindingChatTopicExchangeAndQueue());

    }
}
