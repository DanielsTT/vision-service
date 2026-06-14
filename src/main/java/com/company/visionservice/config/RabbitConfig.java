package com.company.visionservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
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
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setObservationEnabled(true);
        return factory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        rabbitTemplate.setObservationEnabled(true);
        return rabbitTemplate;
    }
}