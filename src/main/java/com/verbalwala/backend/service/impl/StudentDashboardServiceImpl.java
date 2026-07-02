package com.verbalwala.backend.service.impl;

import com.verbalwala.backend.dto.response.ApiResponse;
import com.verbalwala.backend.dto.response.StudentDashboardResponse;
import com.verbalwala.backend.entity.Assessment;
import com.verbalwala.backend.entity.AssessmentAttempt;
import com.verbalwala.backend.entity.User;
import com.verbalwala.backend.enums.AssessmentStatus;
import com.verbalwala.backend.enums.AttemptStatus;
import com.verbalwala.backend.exception.AssessmentNotFoundException;
import com.verbalwala.backend.repository.AssessmentAttemptRepository;
import com.verbalwala.backend.repository.AssessmentRepository;
import com.verbalwala.backend.service.StudentDashboardService;
import com.verbalwala.backend.service.StudentSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentDashboardServiceImpl
        implements StudentDashboardService {

    private final AssessmentAttemptRepository assessmentAttemptRepository;

    private final StudentSecurityService studentSecurityService;

    private final AssessmentRepository assessmentRepository;

    @Override
    public ApiResponse<StudentDashboardResponse> getDashboard() {

        User student =
                studentSecurityService.getCurrentStudent();


        List<AssessmentAttempt> attempts =
                assessmentAttemptRepository.findByStudentId(
                        student.getId()
                );

        int completedAssessments =
                (int) attempts.stream()
                        .filter(a ->
                                a.getStatus() == AttemptStatus.COMPLETED)
                        .count();

        int totalAssessments = (int)assessmentRepository.count();

//        int pendingAssessments =
//                (int) assessmentAttemptRepository
//                        .countByStudentIdAndStatus(
//                                student.getId(),
//                                AttemptStatus.STARTED
//                        );

        int pendingAssessments = totalAssessments - completedAssessments;

        int bestScore =
                attempts.stream()
                        .map(AssessmentAttempt::getObtainedMarks)
                        .filter(score -> score != null)
                        .max(Integer::compareTo)
                        .orElse(0);

        int averageScore =
                (int) attempts.stream()
                        .map(AssessmentAttempt::getObtainedMarks)
                        .filter(score -> score != null)
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0);

        StudentDashboardResponse response =
                StudentDashboardResponse.builder()
                        .completedAssessments(completedAssessments)
                        .pendingAssessments(pendingAssessments)
                        .averageScore(averageScore)
                        .bestScore(bestScore)
                        .build();

        return ApiResponse.<StudentDashboardResponse>builder()
                .success(true)
                .message("Dashboard fetched successfully")
                .data(response)
                .build();
    }
}