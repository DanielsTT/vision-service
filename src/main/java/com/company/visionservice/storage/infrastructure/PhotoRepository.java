package com.company.visionservice.storage.infrastructure;

import com.company.visionservice.storage.domain.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends MongoRepository<Photo, String> {
}