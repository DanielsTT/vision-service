package com.company.visionservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String PHOTO_EXCHANGE = "photo.exchange";
    public static final String PHOTO_ANALYSIS_QUEUE = "photo-analysis-queue";
    public static final String PHOTO_ANALYSIS_ROUTING_KEY = "photo.analysis";

    @Bean
    public Queue photoAnalysisQueue() {
        return QueueBuilder.durable(PHOTO_ANALYSIS_QUEUE).build();
    }

    @Bean
    public TopicExchange photoExchange() {
        return ExchangeBuilder.topicExchange(PHOTO_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding photoAnalysisBinding(Queue photoAnalysisQueue, TopicExchange photoExchange) {
        return BindingBuilder.bind(photoAnalysisQueue)
                .to(photoExchange)
                .with(PHOTO_ANALYSIS_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}