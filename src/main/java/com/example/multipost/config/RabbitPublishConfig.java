package com.example.multipost.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "multipost.rabbit", name = "enabled", havingValue = "true")
public class RabbitPublishConfig {
    public static final String EXCHANGE = "multipost.publish.exchange";
    public static final String QUEUE = "multipost.publish.queue";
    public static final String ROUTING_KEY = "publish.task";

    @Bean
    DirectExchange publishExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    Queue publishQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    Binding publishBinding(Queue publishQueue, DirectExchange publishExchange) {
        return BindingBuilder.bind(publishQueue).to(publishExchange).with(ROUTING_KEY);
    }
}
