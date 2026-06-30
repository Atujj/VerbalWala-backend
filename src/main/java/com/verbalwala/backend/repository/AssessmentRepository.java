package com.verbalwala.backend.repository;

import com.verbalwala.backend.entity.Assessment;
import com.verbalwala.backend.enums.AssessmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Arrays;
import java.util.List;

public interface AssessmentRepository
        extends MongoRepository<Assessment, String> {

    List<Assessment> findByStatus(
            AssessmentStatus status
    );
}