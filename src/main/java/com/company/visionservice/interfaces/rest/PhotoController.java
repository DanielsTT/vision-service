package com.company.visionservice.interfaces.rest;

import com.company.visionservice.storage.application.StorageService;
import com.company.visionservice.storage.domain.Photo;
import com.company.visionservice.storage.infrastructure.PhotoRepository;
import com.company.visionservice.vectordb.application.VectorDbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final StorageService storageService;
    private final VectorDbService vectorDbService;
    private final PhotoRepository photoRepository;

    @PostMapping("/upload")
    public ResponseEntity<Photo> uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Photo savedPhoto = storageService.uploadPhoto(file);
        return ResponseEntity.ok(savedPhoto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Photo>> searchPhotos(@RequestParam("query") String query) {
        List<String> photoIds = vectorDbService.searchPhotoIds(query, 5);

        List<Photo> photosFromDb = photoRepository.findAllById(photoIds);

        List<Photo> sortedPhotos = photoIds.stream()
                .map(id -> photosFromDb.stream()
                        .filter(p -> p.getId().equals(id))
                        .findFirst()
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return ResponseEntity.ok(sortedPhotos);
    }
}