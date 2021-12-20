package top.mikecao.openchat.server.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caohailong
 */

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "chat_topic_exchange";
    public static final String ROUTING_KEY = "chat";
    public static final String QUEUE_NAME = "chat_queue";

    @Bean
    public TopicExchange chatTopicExchange(){
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue chatQueue(){
        return new Queue(QUEUE_NAME, true, false, false);
    }

    @Bean
    public Binding bindingChatTopicExchangeAndQueue(){
        return BindingBuilder.bind(chatQueue()).to(chatTopicExchange()).with(ROUTING_KEY);
    }

}
