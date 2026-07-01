package com.verbalwala.backend.controller;

import com.verbalwala.backend.dto.response.AdminDashboardResponse;
import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public ApiResponse<AdminDashboardResponse> getDashboard() {

        return adminDashboardService.getDashboard();

    }

}