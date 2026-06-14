package com.company.visionservice.storage.infrastructure;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioHealthIndicator implements HealthIndicator {

    private final MinioClient minioClient;

    @Value("${app.minio.bucket-name}")
    private String bucketName;

    @Override
    public Health health() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (exists) {
                return Health.up()
                        .withDetail("bucket", bucketName)
                        .withDetail("status", "Connected & Bucket Accessible")
                        .build();
            }
            return Health.down().withDetail("error", "Bucket missing: " + bucketName).build();
        } catch (Exception e) {
            return Health.down(e).withDetail("status", "MinIO Connection Failed").build();
        }
    }
}