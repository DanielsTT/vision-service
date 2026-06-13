package com.company.visionservice.vectordb.infrastructure;

import io.qdrant.client.QdrantClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QdrantHealthIndicator implements HealthIndicator {

    private final QdrantClient qdrantClient;

    @Value("${spring.ai.vectorstore.qdrant.collection-name}")
    private String collectionName;

    @Override
    public Health health() {
        try {
            boolean exists = qdrantClient.listCollectionsAsync().get().contains(collectionName);
            if (exists) {
                return Health.up()
                        .withDetail("collection", collectionName)
                        .withDetail("status", "Connected & Collection Indexed")
                        .build();
            }
            return Health.down().withDetail("error", "Collection missing: " + collectionName).build();
        } catch (Exception e) {
            return Health.down(e).withDetail("status", "Qdrant gRPC Connection Failed").build();
        }
    }
}