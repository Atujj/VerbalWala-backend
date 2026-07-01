package com.verbalwala.backend.service;

import com.verbalwala.backend.dto.response.AdminAssessmentDetailResponse;
import com.verbalwala.backend.dto.response.ApiResponse;

public interface AdminAssessmentDetailService {

    ApiResponse<AdminAssessmentDetailResponse> getAssessment(
            String assessmentId
    );

}