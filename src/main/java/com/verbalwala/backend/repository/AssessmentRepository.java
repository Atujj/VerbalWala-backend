package com.verbalwala.backend.repository;

import com.verbalwala.backend.entity.Assessment;
import com.verbalwala.backend.entity.AssessmentAttempt;
import com.verbalwala.backend.enums.AssessmentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Arrays;
import java.util.List;

public interface AssessmentRepository
        extends MongoRepository<Assessment, String> {

    List<Assessment> findByStatus(
            AssessmentStatus status
    );


    long countByCreatedById(String createdById);

    long countByCreatedByIdAndStatus(
            String createdById,
            AssessmentStatus status
    );

    List<Assessment> findByCreatedByIdOrderByCreatedAtDesc(
            String createdById
    );

}