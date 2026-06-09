package com.company.visionservice.rabbit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoAnalysisMessage {
    private String photoId;
    private String minioObjectKey;
}