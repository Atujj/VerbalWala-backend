package com.verbalwala.backend.service;

import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.StudentDashboardResponse;

public interface StudentDashboardService {

    ApiResponse<StudentDashboardResponse> getDashboard();

}