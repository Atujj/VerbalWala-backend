package com.verbalwala.backend.service;

import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.StudentAssessmentCardResponse;

import java.util.List;

public interface StudentAssessmentListService {

    ApiResponse<List<StudentAssessmentCardResponse>> getAssessments();

}