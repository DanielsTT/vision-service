package com.company.visionservice.rabbit;

import com.company.visionservice.config.RabbitConfig;
import com.company.visionservice.storage.application.StorageService;
import com.company.visionservice.storage.domain.Photo;
import com.company.visionservice.storage.domain.PhotoStatus;
import com.company.visionservice.storage.infrastructure.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoAnalysisConsumer {

    private final StorageService storageService;
    private final PhotoRepository photoRepository;
    private final ChatModel chatModel;

    @RabbitListener(queues = RabbitConfig.PHOTO_ANALYSIS_QUEUE)
    public void handlePhotoAnalysis(PhotoAnalysisMessage message) {
        log.info("RabbitMQ Worker: Received photo analysis request for ID: {}", message.getPhotoId());

        Photo photo = photoRepository.findById(message.getPhotoId())
                .orElse(null);

        if (photo == null) {
            log.error("Photo with ID {} not found in database. Aborting analysis.", message.getPhotoId());
            return;
        }

        try {
            byte[] imageBytes = storageService.getPhotoBytes(photo.getMinioObjectKey());

            log.info("Sending image to local model for processing...");


            var media = Media.builder()
                    .mimeType(MimeTypeUtils.parseMimeType(photo.getContentType()))
                    .data(new ByteArrayResource(imageBytes))
                    .build();

            var userMessage = UserMessage.builder()
                    .text("Identify the objects in this image. Provide a concise description in English.")
                    .media(media)
                    .build();


            var response = chatModel.call(new Prompt(userMessage));
            String aiResult = response.getResult().getOutput().getText();

            log.info("AI Analysis finished successfully!");

            photo.setAiDescription(aiResult);
            photo.setStatus(PhotoStatus.COMPLETED);
            photoRepository.save(photo);

            log.info("Database updated for photo ID: {} with status COMPLETED", photo.getId());

        } catch (Exception e) {
            log.error("Critical error during AI analysis for photo ID: {}", photo.getId(), e);
            photo.setStatus(PhotoStatus.FAILED);
            photoRepository.save(photo);
        }
    }
}