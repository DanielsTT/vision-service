package com.company.visionservice.rabbit;

import com.company.visionservice.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoAnalysisProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendToAnalysis(PhotoAnalysisMessage message) {
        log.info("Sending photo to RabbitMQ exchange for analysis. Photo ID: {}", message.getPhotoId());
        rabbitTemplate.convertAndSend(
                RabbitConfig.PHOTO_EXCHANGE,
                RabbitConfig.PHOTO_ANALYSIS_ROUTING_KEY,
                message
        );
    }
}