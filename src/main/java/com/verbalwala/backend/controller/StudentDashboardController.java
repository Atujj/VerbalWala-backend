package com.verbalwala.backend.controller;

import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.StudentDashboardResponse;
import com.verbalwala.backend.service.StudentDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/dashboard")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentDashboardService studentDashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<StudentDashboardResponse>> getDashboard() {

        return ResponseEntity.ok(
                studentDashboardService.getDashboard()
        );
    }
}