package com.company.visionservice.storage.application;

import com.company.visionservice.rabbit.PhotoAnalysisMessage;
import com.company.visionservice.rabbit.PhotoAnalysisProducer;
import com.company.visionservice.storage.domain.Photo;
import com.company.visionservice.storage.domain.PhotoStatus;
import com.company.visionservice.storage.infrastructure.PhotoRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioClient minioClient;
    private final PhotoRepository photoRepository;
    private final PhotoAnalysisProducer photoAnalysisProducer;

    @Value("${app.minio.bucket-name}")
    private String bucketName;

    public Photo uploadPhoto(MultipartFile file) {
        String photoId = UUID.randomUUID().toString();
        String objectKey = photoId + "_" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectKey)
                            .stream(inputStream, file.getSize(), -1L)
                            .contentType(file.getContentType())
                            .build()
            );

            Photo photo = Photo.builder()
                    .id(photoId)
                    .fileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .minioObjectKey(objectKey)
                    .status(PhotoStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            Photo savedPhoto = photoRepository.save(photo);

            photoAnalysisProducer.sendToAnalysis(new PhotoAnalysisMessage(
                    savedPhoto.getId(),
                    savedPhoto.getMinioObjectKey()
            ));

            return savedPhoto;

        } catch (Exception e) {
            throw new RuntimeException("Error while saving file in MinIO object cloud", e);
        }
    }
}