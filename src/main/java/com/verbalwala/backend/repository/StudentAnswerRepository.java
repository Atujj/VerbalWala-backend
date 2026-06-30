package com.verbalwala.backend.repository;

import com.verbalwala.backend.entity.StudentAnswer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StudentAnswerRepository
        extends MongoRepository<StudentAnswer, String> {

    List<StudentAnswer> findByAttemptId(String attemptId);

    void deleteByAttemptId(String attemptId);

}