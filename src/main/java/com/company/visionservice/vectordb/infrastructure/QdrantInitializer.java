package com.company.visionservice.vectordb.infrastructure;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class QdrantInitializer {

    private final QdrantClient qdrantClient;

    @Value("${spring.ai.vectorstore.qdrant.collection-name}")
    private String collectionName;

    @PostConstruct
    public void initCollection() {
        try {
            log.info("Qdrant Initializer: Checking if collection '{}' exists...", collectionName);
            
            boolean exists = qdrantClient.listCollectionsAsync().get().contains(collectionName);

            if (!exists) {
                log.info("Qdrant collection '{}' not found. Creating a new one...", collectionName);

                qdrantClient.createCollectionAsync(
                        collectionName,
                        VectorParams.newBuilder()
                                .setSize(768)
                                .setDistance(Distance.Cosine)
                                .build()
                ).get();

                log.info("Qdrant collection '{}' successfully created and initialized.", collectionName);
            } else {
                log.info("Qdrant collection '{}' already exists. Skipping initialization.", collectionName);
            }

        } catch (Exception e) {
            log.error("Critical error during Qdrant collection '{}' schema initialization!", collectionName, e);
        }
    }
}