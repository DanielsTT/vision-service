package com.company.visionservice.vectordb.application;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VectorDbService {

    private final VectorStore vectorStore;

    public void savePhotoDescription(String photoId, String description) {
        Document document = new Document(
                description,
                Map.of("photoId", photoId)
        );
        vectorStore.add(List.of(document));
    }

    public List<String> searchPhotoIds(String query, int topK) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();

        List<Document> results = vectorStore.similaritySearch(searchRequest);

        return results.stream()
                .map(doc -> (String) doc.getMetadata().get("photoId"))
                .filter(Objects::nonNull)
                .toList();
    }
}