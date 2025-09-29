package org.example.ecom.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "order_exchange";
    public static final String QUEUE = "notification_queue";
    public static final String ROUTING_KEY = "order.notification";
    public static final String DLQ = "notification_dlq";

    // Exchange principal
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // Dead Letter Queue
    @Bean
    public Queue notificationDlq() {
        return QueueBuilder.durable(DLQ).build();
    }

    // Queue principale reliée à la DLQ
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    // Binding Queue -> Exchange
    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange exchange) {
        return BindingBuilder.bind(notificationQueue).to(exchange).with(ROUTING_KEY);
    }
}

