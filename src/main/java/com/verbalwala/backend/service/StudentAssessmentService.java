package com.verbalwala.backend.service;

import com.verbalwala.backend.dto.request.SubmitEmailRequest;
import com.verbalwala.backend.dto.request.SubmitFillBlankRequest;
import com.verbalwala.backend.dto.request.SubmitPassageRequest;
import com.verbalwala.backend.dto.request.TerminateAssessmentRequest;
import com.verbalwala.backend.dto.response.*;

public interface StudentAssessmentService {

    ApiResponse<StartAssessmentResponse> startAssessment(
            String assessmentId
    );

    ApiResponse<PassageResponse> submitFillBlanks(
            String attemptId,
            SubmitFillBlankRequest request
    );

    ApiResponse<EmailResponse> submitPassage(
            String attemptId,
            SubmitPassageRequest request
    );

    ApiResponse<AssessmentSubmittedResponse> submitEmail(
            String attemptId,
            SubmitEmailRequest request
    );

    ApiResponse<Void> terminateAssessment(
            String attemptId,
            TerminateAssessmentRequest request
    );





}