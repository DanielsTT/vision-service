package com.company.visionservice.interfaces.rest;

import com.company.visionservice.storage.application.StorageService;
import com.company.visionservice.storage.domain.Photo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<Photo> uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Photo savedPhoto = storageService.uploadPhoto(file);
        return ResponseEntity.ok(savedPhoto);
    }
}