package com.verbalwala.backend.service;

import com.verbalwala.backend.dto.response.AdminAssessmentCardResponse;
import com.verbalwala.backend.dto.response.ApiResponse;

import java.util.List;

public interface AdminAssessmentListService {

    ApiResponse<List<AdminAssessmentCardResponse>> getAssessments();

}