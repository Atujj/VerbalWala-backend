package com.verbalwala.backend.service;

import com.verbalwala.backend.dto.response.AssessmentResultResponse;

public interface EvaluationService {

    void evaluateAttempt(String attemptId);

    AssessmentResultResponse getResult(String attemptId);

    void evaluateAttemptAsync(String attemptId);
}