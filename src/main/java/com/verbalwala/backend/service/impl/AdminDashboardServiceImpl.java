package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.dto.response.AdminDashboardResponse;
import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.enums.AssessmentStatus;
import com.verbalwala.backend.enums.Role;
import com.verbalwala.backend.repository.AssessmentRepository;
import com.verbalwala.backend.repository.UserRepository;
import com.verbalwala.backend.service.AdminDashboardService;
import com.verbalwala.backend.service.AdminSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final AssessmentRepository assessmentRepository;

    private final UserRepository userRepository;

    private final AdminSecurityService adminSecurityService;

    @Override
    public ApiResponse<AdminDashboardResponse> getDashboard() {

        User admin =
                adminSecurityService.getCurrentAdmin();

        AdminDashboardResponse response =
                AdminDashboardResponse.builder()

                        .totalAssessments(
                                (int) assessmentRepository
                                        .countByCreatedById(
                                                admin.getId()
                                        )
                        )

                        .publishedAssessments(
                                (int) assessmentRepository
                                        .countByCreatedByIdAndStatus(
                                                admin.getId(),
                                                AssessmentStatus.PUBLISHED
                                        )
                        )

                        .totalStudents(
                                (int) userRepository
                                        .countByRole(
                                                Role.STUDENT
                                        )
                        )

                        .build();

        return ApiResponse.<AdminDashboardResponse>builder()
                .success(true)
                .message("Dashboard fetched successfully")
                .data(response)
                .build();
    }
}