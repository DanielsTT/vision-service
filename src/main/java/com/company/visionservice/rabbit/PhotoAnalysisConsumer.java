package com.company.visionservice.rabbit;

import com.company.visionservice.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PhotoAnalysisConsumer {

    @RabbitListener(queues = RabbitConfig.PHOTO_ANALYSIS_QUEUE)
    public void handlePhotoAnalysis(PhotoAnalysisMessage message) {
        log.info("==========================================================================");
        log.info("RabbitMQ Worker: Received photo task for background AI analysis.");
        log.info("Processing Photo ID: {}", message.getPhotoId());
        log.info("Target MinIO Object Key: {}", message.getMinioObjectKey());
        log.info("==========================================================================");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("RabbitMQ Worker: Simulation of background analysis for photo {} finished successfully!", message.getPhotoId());
    }
}