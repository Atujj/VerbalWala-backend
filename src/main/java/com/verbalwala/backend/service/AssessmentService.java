package com.verbalwala.backend.service;

import com.verbalwala.backend.dto.request.CreateAssessmentRequest;
import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.AssessmentStudentResponse;

import java.util.List;

public interface AssessmentService {

    ApiResponse<String> createAssessment(CreateAssessmentRequest request);

    ApiResponse<Void> publishAssessment(String assessmentId);

    List<AssessmentStudentResponse> getStudents(
            String assessmentId
    );

}