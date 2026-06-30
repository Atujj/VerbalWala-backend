package com.verbalwala.backend.repository;

import com.verbalwala.backend.entity.AssessmentAttempt;
import com.verbalwala.backend.enums.AttemptStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentAttemptRepository
        extends MongoRepository<AssessmentAttempt, String> {

    List<AssessmentAttempt> findByStudentId(String studentId);

    Optional<AssessmentAttempt> findTopByAssessmentIdAndStudentIdOrderByAttemptNumberDesc(
            String assessmentId,
            String studentId
    );

    Optional<AssessmentAttempt> findByAssessmentIdAndStudentIdAndStatus(
            String assessmentId,
            String studentId,
            AttemptStatus status
    );


    long countByStudentIdAndStatus(
            String studentId,
            AttemptStatus status
    );

    List<AssessmentAttempt>
    findByAssessmentIdAndStudentIdOrderByAttemptNumberAsc(
            String assessmentId,
            String studentId
    );

    long countByAssessmentIdAndStudentIdAndStatusIn(
            String assessmentId,
            String studentId,
            List<AttemptStatus> statuses
    );



}