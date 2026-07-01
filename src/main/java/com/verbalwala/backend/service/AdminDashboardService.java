package com.verbalwala.backend.service;

import com.verbalwala.backend.dto.response.AdminDashboardResponse;
import com.verbalwala.backend.dto.response.ApiResponse;

public interface AdminDashboardService {

    ApiResponse<AdminDashboardResponse> getDashboard();

}