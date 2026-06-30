package com.verbalwala.backend.repository;

import com.verbalwala.backend.entity.Question;
import com.verbalwala.backend.enums.QuestionType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface QuestionRepository extends MongoRepository<Question, String> {

    List<Question> findByAssessmentIdOrderByQuestionOrder(String assessmentId);

    List<Question> findByAssessmentIdAndTypeOrderByQuestionOrder(
            String assessmentId,
            QuestionType type
    );

    long countByAssessmentId(String assessmentId);



}