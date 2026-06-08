package com.company.visionservice.storage.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "photos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Photo {
    @Id
    private String id;
    private String fileName;
    private String contentType;
    private long size;
    private String minioObjectKey;
    private PhotoStatus status;
    private LocalDateTime createdAt;
}